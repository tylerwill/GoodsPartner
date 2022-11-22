package com.goodspartner.service.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.util.retry.Retry;

import java.io.IOException;
import java.net.URI;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.StringJoiner;
import java.util.function.Supplier;
import java.util.stream.Collectors;

@AllArgsConstructor
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

    @Value("classpath:mock1CoData/*")
    private Resource[] mockFiles;

    private final GrandeDolce1CProperties properties;

    private final WebClient webClient;
    private final ODataOrderMapper odataOrderMapper;
    private final ProductMapper productMapper;
    private final ExternalOrderDataEnricher enricher;
    private final ObjectMapper objectMapper;


    @Override
    public List<OrderDto> findAllByShippingDate(LocalDate deliveryDate) {
        log.info("Start fetching orders for date: {}", deliveryDate);
        long startTime = System.currentTimeMillis();

        ODataWrapperDto<ODataOrderDto> oDataWrappedOrderDtos = getOrders(deliveryDate);

        List<ODataOrderDto> oDataOrderDtosList = oDataWrappedOrderDtos.getValue();

        List<List<String>> partitionedOrders = getPartitionOfProductKeys(oDataOrderDtosList);

        Map<String, List<ODataProductDto>> allProducts = getProductsByOrders(partitionedOrders);

        enrichOrders(oDataOrderDtosList, allProducts);

        log.info("{} Orders has been fetched from 1C for date: {} in {}",
                oDataOrderDtosList.size(), deliveryDate, System.currentTimeMillis() - startTime);

        return odataOrderMapper.toOrderDtosList(oDataOrderDtosList);
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
     */
    private ODataWrapperDto<ODataOrderDto> getOrders(LocalDate deliveryDate) {
        URI orderUri = buildOrderUri(createOrderByDateFilter(deliveryDate.atStartOfDay().toString()));

        return fetchMockDataByRequest(orderUri, new TypeReference<>() {
                },
                () -> webClient.get()
                        .uri(orderUri)
//                .headers(httpHeaders -> httpHeaders.setBasicAuth(properties.getLogin(), properties.getPassword()))
                        .retrieve()
                        .bodyToMono(new ParameterizedTypeReference<ODataWrapperDto<ODataOrderDto>>() {
                        })
                        .retryWhen(Retry.backoff(3, Duration.ofSeconds(15)))
                        .block());
    }

    /**
     * Get products from 1C
     */
    private ODataWrapperDto<ODataProductDto> getProducts(URI productUri) {
        return fetchMockDataByRequest(productUri, new TypeReference<>() {
                },
                () -> webClient.get()
                        .uri(productUri)
//                .headers(httpHeaders -> httpHeaders.setBasicAuth(properties.getLogin(), properties.getPassword()))
                        .retrieve()
                        .bodyToMono(new ParameterizedTypeReference<ODataWrapperDto<ODataProductDto>>() {
                        })
                        .retryWhen(Retry.backoff(3, Duration.ofSeconds(15)))
                        .block());
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
     */
    private Map<String, List<ODataProductDto>> getProductsByOrders(List<List<String>> partition) {
        return partition.stream()
                .map(this::createProductsFilter)
                .map(this::buildProductUri)
                .map(this::getProducts)
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

    private <T> ODataWrapperDto<T> fetchMockDataByRequest(URI integrationServiceUri,
                                                          TypeReference<ODataWrapperDto<T>> typeRef,
                                                          Supplier<ODataWrapperDto<T>> supplier) {
        return Arrays.stream(mockFiles)
                .filter(resource -> Objects.nonNull(resource.getFilename()))
                .filter(resource -> checkIfMockResourceMathRequest(integrationServiceUri, resource))
                .findFirst()
                .map(resource -> {
                    try {
                        return objectMapper.readValue(
                                FileUtils.readFileToString(resource.getFile(), StandardCharsets.UTF_8),
                                typeRef
                        );
                    } catch (IOException e) {
                        log.error("Unable to read the file: {} fallback call to service", resource.getFilename());
                        return null;
                    }
                })
                .orElseGet(supplier);
    }

    private boolean checkIfMockResourceMathRequest(URI integrationServiceUri, Resource resource) {
        String decodedUrlQuery = URLDecoder.decode(integrationServiceUri.toString(), StandardCharsets.UTF_8);
        String mockFileName = FilenameUtils.removeExtension(resource.getFilename());
        boolean found = decodedUrlQuery.contains(mockFileName == null ? "" : mockFileName);
        log.debug("Trying to find mockFile by url: {} - {} -> {}", decodedUrlQuery, mockFileName, found);
        return found;
    }
}