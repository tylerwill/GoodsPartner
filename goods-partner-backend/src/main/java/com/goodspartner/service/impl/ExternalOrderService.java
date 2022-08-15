package com.goodspartner.service.impl;

import com.goodspartner.dto.OrderDto;
import com.goodspartner.dto.ProductDto;
import com.goodspartner.service.OrderService;
import com.google.common.collect.Lists;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.olingo.client.api.ODataClient;
import org.apache.olingo.client.api.communication.request.retrieve.ODataEntitySetRequest;
import org.apache.olingo.client.api.communication.request.retrieve.RetrieveRequestFactory;
import org.apache.olingo.client.api.communication.response.ODataRetrieveResponse;
import org.apache.olingo.client.api.domain.ClientEntity;
import org.apache.olingo.client.api.domain.ClientEntitySet;
import org.apache.olingo.client.api.domain.ClientPrimitiveValue;
import org.apache.olingo.client.api.domain.ClientProperty;
import org.apache.olingo.client.api.domain.ClientValue;
import org.apache.olingo.client.core.ODataClientFactory;
import org.apache.olingo.commons.api.edm.EdmPrimitiveTypeException;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;
import java.util.function.Function;
import java.util.stream.Collector;
import java.util.stream.Collectors;

@AllArgsConstructor
@Service
@Slf4j
public class ExternalOrderService implements OrderService {

    private static final int PARTITION_SIZE = 100;
    private static final int ORDER_FETCH_LIMIT = 9; // API Key limit
    private static final String FORMAT = "json";
    // Order
    private static final String ORDER_ENTRY_SET_NAME = "Document_ЗаказПокупателя";
    private static final String ORDER_SELECT_FIELDS = "Ref_Key,Number,Date,АдресДоставки,Контрагент/Description,Ответственный/Code";
    private static final String ORDER_EXPAND_FIELDS = "Ответственный/ФизЛицо,Контрагент";
    // Product
    private static final String PRODUCT_ENTRY_SET_NAME = "Document_ЗаказПокупателя_Товары";
    private static final String PRODUCT_SELECT_FIELDS = "Ref_Key,Количество,КоличествоМест,Коэффициент,ЕдиницаИзмерения/Description,Номенклатура/Description";
    private static final String PRODUCT_EXPAND_FIELDS = "Номенклатура,ЕдиницаИзмерения";

    public static final String SERVER_ODATA_URL = "http://89.76.239.245:8080/test/odata/standard.odata/";

    private final ODataClient client = ODataClientFactory.getClient();

    private final MockedStoreService mockedStoreService;

    @Override
    public List<OrderDto> findAllByShippingDate(LocalDate date) {
        long startTime = System.currentTimeMillis();

        URI orderUri = buildOrderUri(createOrderByDateFilter(date.atStartOfDay().toString()));
        ClientEntitySet clientEntitySet = readEntities(orderUri);

        List<Map<String, ClientValue>> clientMap = clientEntitySetToListOfMap(clientEntitySet);

        Map<String, OrderDto> orders = clientMap.stream()
                .map(this::parseOrder)
                .collect(Collectors.toMap(Pair::getFirst, Pair::getSecond, (v1, v2) -> v1));

        List<List<String>> partitionedOrders = getPartitionOfProductKeys(orders);

        // String with Ref_Keys -> Prepared URL -> ClientEntrySet -> List<Map<ClientPropertyName, ClintProperty>
        // Map<Ref_key, List<ProductDto> -> collect maps to single map
        Map<String, List<ProductDto>> allProducts = parseAllProducts(partitionedOrders);

        enrichOrders(orders, allProducts);

        log.info("Orders has been fetched from 1C for date: {} in {}", date, System.currentTimeMillis() - startTime);
        return orders.values().stream().toList();

    }

    /*URI*/
    private URI buildOrderUri(String filter) {
        return client.newURIBuilder(SERVER_ODATA_URL)
                .appendEntitySetSegment(ORDER_ENTRY_SET_NAME)
                .filter(filter)
                .expand(ORDER_EXPAND_FIELDS)
                .select(ORDER_SELECT_FIELDS)
                .format(FORMAT)
                .top(ORDER_FETCH_LIMIT)
                .build();
    }

    private URI buildProductUri(String filter) {
        return client.newURIBuilder(SERVER_ODATA_URL)
                .appendEntitySetSegment(PRODUCT_ENTRY_SET_NAME)
                .filter(filter)
                .expand(PRODUCT_EXPAND_FIELDS)
                .select(PRODUCT_SELECT_FIELDS)
                .format(FORMAT)
                .build();
    }

    private ClientEntitySet readEntities(URI absoluteUri) {
        log.info("URI = " + java.net.URLDecoder.decode(String.valueOf(absoluteUri), StandardCharsets.UTF_8));
        RetrieveRequestFactory retrieveRequestFactory = client.getRetrieveRequestFactory();
        ODataEntitySetRequest<ClientEntitySet> entitySetRequest = retrieveRequestFactory.getEntitySetRequest(absoluteUri);
        ODataRetrieveResponse<ClientEntitySet> response = entitySetRequest.execute();
        return response.getBody();
    }

    private List<Map<String, ClientValue>> clientEntitySetToListOfMap(ClientEntitySet clientEntitySet) {
        return clientEntitySet.getEntities().stream()
                .map(ClientEntity::getProperties)
                .map(clientProperties -> clientProperties.stream()
                        .collect(Collectors.toMap(ClientProperty::getName, ClientProperty::getValue)))
                .toList();
    }

    private Pair<String, OrderDto> parseOrder(Map<String, ClientValue> orderProperties) {
        String refKey = parsePrimitive(orderProperties.get("Ref_Key"));

        OrderDto order = OrderDto.builder()
                .orderNumber(parsePrimitive(orderProperties.get("Number")))
                .createdDate(parseDate(orderProperties.get("Date")))
                .address(parsePrimitive(orderProperties.get("АдресДоставки")))
                .clientName(parseComplex(orderProperties.get("Контрагент")))
                .managerFullName(parseComplex(orderProperties.get("Ответственный")).trim())
                .build();

        return Pair.of(refKey, order);
    }

    private List<List<String>> getPartitionOfProductKeys(Map<String, OrderDto> orders) {
        return Lists.partition(orders.keySet().stream().toList(), PARTITION_SIZE);
    }

    private Map<String, List<ProductDto>> parseAllProducts(List<List<String>> partition) {
        return partition.stream()
                .map(this::createProductsFilter)
                .map(this::buildProductUri)
                .map(this::readEntities)
                .map(this::clientEntitySetToListOfMap)
                .map(mapOfProductListFunction())
                .flatMap(map -> map.entrySet().stream())
                .collect(productMapCollector());
    }

    private Function<List<Map<String, ClientValue>>, Map<String, List<ProductDto>>> mapOfProductListFunction() {
        return product -> product.stream()
                .map(this::parseProduct)
                .collect(PairToMapCollector());
    }

    private Collector<Pair<String, ProductDto>, ?, Map<String, List<ProductDto>>> PairToMapCollector() {
        return Collectors.groupingBy(Pair::getFirst, Collectors.mapping(Pair::getSecond, Collectors.toList()));
    }

    private Pair<String, ProductDto> parseProduct(Map<String, ClientValue> productProperties) {
        String key = parsePrimitive(productProperties.get("Ref_Key"));
        List<String> allowableMeasure = List.of("кг", "л", "шт");
        int amount = Integer.parseInt(parsePrimitive(productProperties.get("КоличествоМест")));
        String measure = parseComplex(productProperties.get("ЕдиницаИзмерения"));
        double totalWeight = allowableMeasure.contains(measure) ?
                (!measure.equals("шт") ?
                        Double.parseDouble(parsePrimitive(productProperties.get("Количество")).toString()) :
                        1.0)
                : 0.0;

        ProductDto product = ProductDto.builder()
                .productName(parseComplex(productProperties.get("Номенклатура")))
                .amount(amount)
                .unitWeight(amount == 0 ? 0 : totalWeight / amount)
                .totalProductWeight(totalWeight)
                .storeName(mockedStoreService.getMainStore().getName())
                .build();
        return Pair.of(key, product);
    }

    private Collector<Map.Entry<String, List<ProductDto>>, ?, Map<String, List<ProductDto>>> productMapCollector() {
        return Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (v1, v2) -> v1);
    }

    private void enrichOrders(Map<String, OrderDto> orders, Map<String, List<ProductDto>> allProducts) {
        for (Map.Entry<String, OrderDto> entry: orders.entrySet()) {
            String key = entry.getKey();
            OrderDto order = entry.getValue();
            List<ProductDto> products = allProducts.get(key);
            order.setProducts(products);
            order.setOrderWeight(getTotalOrderWeight(products));
        }
    }

    private double getTotalOrderWeight(List<ProductDto> products) {
        return products.stream()
                .mapToDouble(ProductDto::getTotalProductWeight)
                .sum();
    }

    /**
     * Filters
     */
    private String createProductsFilter(List<String> refKeys) {
        StringJoiner stringJoiner = new StringJoiner(" or ");
        refKeys.forEach(
                key -> stringJoiner.add(createRefKeyFilterRequest(key))
        );

        return stringJoiner.toString();
    }

    private String createRefKeyFilterRequest(String key) {
        return String.format("Ref_Key eq guid'%s'", key);
    }

    /*
    Not empty line and minimum 6 symbols - "м.Київ"
     */
    private String createOrderByDateFilter(String date) {
        return String.format("ДатаОтгрузки eq datetime'%s' and like(АдресДоставки , '[^\"\"][^\"   \"]______%%')", date);
    }


    /**
     * Parsers
     */

    private <T> T parsePrimitive(ClientValue value) {
        ClientPrimitiveValue clientPrimitiveValue = value.asPrimitive();
        Class<?> clazz = clientPrimitiveValue.getType().getDefaultType();

        try {
            return (T) clientPrimitiveValue.toCastValue(clazz);
        } catch (EdmPrimitiveTypeException exception) {
            throw new RuntimeException(String.format("Error during parsing primitive type - %s", exception));
        }
    }

    private LocalDate parseDate(ClientValue date) {
        return LocalDateTime
                .parse(parsePrimitive(date))
                .toLocalDate();
    }

    /*Parse olingo client values*/
    private String parseComplex(ClientValue value) {
        return (String) value
                .asComplex()
                .asJavaMap()
                .values()
                .toArray()[0];
    }


}
