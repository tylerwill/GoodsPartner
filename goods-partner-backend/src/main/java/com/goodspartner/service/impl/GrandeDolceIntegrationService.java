package com.goodspartner.service.impl;

import com.goodspartner.configuration.properties.GrandeDolce1CProperties;
import com.goodspartner.dto.OrderDto;
import com.goodspartner.mapper.ODataOrderMapper;
import com.goodspartner.mapper.ProductMapper;
import com.goodspartner.service.IntegrationService;
import com.goodspartner.service.dto.external.grandedolce.ODataOrderDto;
import com.goodspartner.service.dto.external.grandedolce.ODataProductDto;
import com.goodspartner.service.dto.external.grandedolce.ODataWrapperDto;
import com.goodspartner.util.ExternalOrderDataEnricher;
import com.goodspartner.util.ODataUrlBuilder;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.util.retry.Retry;

import java.net.URI;
import java.time.Duration;
import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;
import java.util.stream.Collectors;

@Service
@Slf4j
public class GrandeDolceIntegrationService implements IntegrationService {

    private static final int PARTITION_SIZE = 100;
    private static final int ORDER_FETCH_LIMIT = Integer.MAX_VALUE; // API Key limit
    private static final String HOST_PREFIX = "test/odata/standard.odata/";
    private static final String FORMAT = "json";
    // Order
    private static final String ORDER_ENTRY_SET_NAME = "Document_ЗаказПокупателя";
    private static final String ORDER_SELECT_FIELDS = "Ref_Key,Number,Date,АдресДоставки,Комментарий,Контрагент/Description,Ответственный/Code";
    private static final String ORDER_EXPAND_FIELDS = "Ответственный/ФизЛицо,Контрагент";
    // Product
    private static final String PRODUCT_ENTRY_SET_NAME = "Document_ЗаказПокупателя_Товары";
    private static final String PRODUCT_SELECT_FIELDS = "Ref_Key,Количество,КоличествоМест,Коэффициент,ЕдиницаИзмерения/Description,Номенклатура/Description";
    private static final String PRODUCT_EXPAND_FIELDS = "Номенклатура,ЕдиницаИзмерения";

    private final GrandeDolce1CProperties properties;

    private final WebClient webClient;
    private final ODataOrderMapper odataOrderMapper;
    private final ProductMapper productMapper;
    private final ExternalOrderDataEnricher enricher;

    public GrandeDolceIntegrationService(GrandeDolce1CProperties properties,
                                         WebClient webClient,
                                         ODataOrderMapper odataOrderMapper,
                                         ProductMapper productMapper,
                                         ExternalOrderDataEnricher enricher) {
        this.properties = properties;
        this.webClient = webClient;
        this.odataOrderMapper = odataOrderMapper;
        this.productMapper = productMapper;
        this.enricher = enricher;
    }

    @Override
    public List<OrderDto> findAllByShippingDate(LocalDate deliveryDate) {
        long startTime = System.currentTimeMillis();

        URI orderUri = buildOrderUri(createOrderByDateFilter(deliveryDate.atStartOfDay().toString()));

        ODataWrapperDto<ODataOrderDto> oDataWrappedOrderDtos = getOrders(orderUri);

        List<ODataOrderDto> oDataOrderDtosList = oDataWrappedOrderDtos.getValue();

        List<List<String>> partitionedOrders = getPartitionOfProductKeys(oDataOrderDtosList);

        Map<String, List<ODataProductDto>> allProducts = parseAllProducts(partitionedOrders);

        enrichOrders(oDataOrderDtosList, allProducts);

        List<OrderDto> orderDtosList = odataOrderMapper.toOrderDtosList(oDataOrderDtosList);

        log.info("{} Orders has been fetched from 1C for date: {} in {}",
                orderDtosList.size(), deliveryDate, System.currentTimeMillis() - startTime);

        return orderDtosList;
    }

    @Override
    public double calculateTotalOrdersWeight(List<OrderDto> ordersByDate) {
        return ordersByDate
                .stream()
                .mapToDouble(OrderDto::getOrderWeight)
                .sum();
    }

    /**
     * Get orders from 1C
     *
     * @param uri for receive Document_ЗаказПокупателя from 1C
     * @return ODataWrapperDto<ODataOrderDto> wrapper with ProductDto for OData
     */
    private ODataWrapperDto<ODataOrderDto> getOrders(URI orderUri) {
        return webClient.get()
                .uri(orderUri)
//                .headers(httpHeaders -> httpHeaders.setBasicAuth(properties.getLogin(), properties.getPassword()))
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<ODataWrapperDto<ODataOrderDto>>() {
                })
                .retryWhen(Retry.backoff(3, Duration.ofSeconds(15)))
                .block();
    }

    /**
     * Get products from 1C
     *
     * @param uri for receive Document_ЗаказПокупателя_Товары from 1C
     * @return ODataWrapperDto<ODataProductDto> wrapper with ProductDto for OData
     */
    private ODataWrapperDto<ODataProductDto> parseProducts(URI productUri) {
        return webClient.get()
                .uri(productUri)
//                .headers(httpHeaders -> httpHeaders.setBasicAuth(properties.getLogin(), properties.getPassword()))
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<ODataWrapperDto<ODataProductDto>>() {
                })
                .retryWhen(Retry.backoff(3, Duration.ofSeconds(15)))
                .block();
    }

    /*URI*/
    private URI buildOrderUri(String filter) {
        return new ODataUrlBuilder()
                .baseUrl(properties.getUrl())
                .hostPrefix(HOST_PREFIX)
                .appendEntitySetSegment(ORDER_ENTRY_SET_NAME)
                .filter(filter)
                .expand(ORDER_EXPAND_FIELDS)
                .select(ORDER_SELECT_FIELDS)
                .format(FORMAT)
                .top(ORDER_FETCH_LIMIT)
                .build();
    }

    private URI buildProductUri(String filter) {
        return new ODataUrlBuilder()
                .baseUrl(properties.getUrl())
                .hostPrefix(HOST_PREFIX)
                .appendEntitySetSegment(PRODUCT_ENTRY_SET_NAME)
                .filter(filter)
                .expand(PRODUCT_EXPAND_FIELDS)
                .select(PRODUCT_SELECT_FIELDS)
                .format(FORMAT)
                .build();
    }

    /**
     * If amount of orders would be huge, for possibility to send request for retrieving products -
     * split on partition of refKeys list for further creation filter for OData request
     *
     * @param List<ODataOrderDto> orders
     * @return List<List < String>>
     */
    private List<List<String>> getPartitionOfProductKeys(List<ODataOrderDto> orders) {
        List<String> listRefKeys = orders.stream()
                .map(ODataOrderDto::getRefKey)
                .collect(Collectors.toList());
        return Lists.partition(listRefKeys, PARTITION_SIZE);
    }

    /**
     * - From partitions (list of refKeys) create filter
     * - build appropriate URI
     * - get response and parse it to ODataWrapperDto<ODataProductDto>
     * - collect to Map<RefKey, List<ProductDto>>
     *
     * @param List<List<String>>partition
     * @return Map<String, List < ProductDto>>
     */
    private Map<String, List<ODataProductDto>> parseAllProducts(List<List<String>> partition) {
        return partition.stream()
                .map(this::createProductsFilter)
                .map(this::buildProductUri)
                .map(this::parseProducts)
                .map(ODataWrapperDto::getValue)
                .flatMap(Collection::stream)
                .peek(enricher::enrichODataProduct)
                .collect(Collectors.groupingBy(ODataProductDto::getRefKey, Collectors.toList()));
    }

    /**
     * Insert into OrderDto its List of products and total weight of order
     */
    void enrichOrders(List<ODataOrderDto> orders,
                      Map<String, List<ODataProductDto>> allProducts) {
        for (ODataOrderDto order : orders) {
            String refKey = order.getRefKey();
            List<ODataProductDto> products = allProducts.get(refKey);
            order.setProducts(productMapper.toProductDtosList(products));
            order.setOrderWeight(getTotalOrderWeight(products));
        }
    }

    double getTotalOrderWeight(List<ODataProductDto> products) {
        return products.stream()
                .mapToDouble(ODataProductDto::getTotalProductWeight)
                .sum();
    }

    /**
     * Filters
     */
    String createProductsFilter(List<String> refKeys) {
        StringJoiner stringJoiner = new StringJoiner(" or ");
        refKeys.forEach(
                key -> stringJoiner.add(createRefKeyFilterRequest(key))
        );

        return stringJoiner.toString();
    }

    String createRefKeyFilterRequest(String key) {
        return String.format("Ref_Key eq guid'%s'", key);
    }

    /*
    Not empty line and minimum 6 symbols - "м.Київ"
     */
    String createOrderByDateFilter(String date) {
        return String.format("ДатаОтгрузки eq datetime'%s' and like(АдресДоставки , '[^\"\"][^\"   \"]______%%')", date);
    }
}