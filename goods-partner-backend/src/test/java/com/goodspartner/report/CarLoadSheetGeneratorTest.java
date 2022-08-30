package com.goodspartner.report;

import com.goodspartner.dto.CarDto;
import com.goodspartner.dto.OrderDto;
import com.goodspartner.dto.ProductDto;
import com.goodspartner.web.controller.response.RoutesCalculation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.util.Pair;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CarLoadSheetGeneratorTest {

    private final CarLoadSheetGenerator carLoadSheetGenerator = new CarLoadSheetGenerator();

    private RoutesCalculation.CarLoadDto carLoadDto;
    private ProductDto productSecond;
    private ProductDto productThird;

    @BeforeEach
    public void setUp() {
        CarDto carDto = CarDto.builder()
                .id(1)
                .name("Mercedes Vito")
                .driver("Ivan Piddubny")
                .licencePlate("AA 2222 CT")
                .travelCost(10)
                .available(true)
                .cooler(false)
                .weightCapacity(1000)
                .loadSize(59.32)
                .build();

        ProductDto productFirst = ProductDto.builder()
                .productName("3434 Паста шоколадна")
                .amount(1)
                .unitWeight(1.52)
                .totalProductWeight(1.52)
                .build();

        productSecond = ProductDto.builder()
                .productName("678968 Суміш для випікання Мрія Маффіни з апельсиновою цедрою")
                .amount(5)
                .unitWeight(57.8)
                .totalProductWeight(289)
                .build();

        productThird = ProductDto.builder()
                .productName("678968 Суміш для випікання Мрія Маффіни з апельсиновою цедрою")
                .amount(10)
                .unitWeight(47.8)
                .totalProductWeight(478)
                .build();

        OrderDto orderFirst = OrderDto.builder()
                .products(List.of(productFirst, productSecond, productThird))
                .orderNumber(String.valueOf(35665))
                .orderWeight(579.52)
                .id(1)
                .build();
        OrderDto orderSecond = OrderDto.builder()
                .products(List.of(productThird))
                .orderNumber(String.valueOf(36325))
                .orderWeight(1.52)
                .id(2)
                .build();

        carLoadDto = RoutesCalculation.CarLoadDto.builder()
                .car(carDto)
                .orders(List.of(orderFirst, orderSecond))
                .build();
    }

    @Test
    public void testGroupByProducts() {
        Map<String, List<Pair<String, ProductDto>>> map = carLoadSheetGenerator.groupByProducts(carLoadDto);

        List<Pair<String, ProductDto>> chocolatePasteList = map.remove("3434 Паста шоколадна");
        assertEquals(1, chocolatePasteList.size());
        Set<String> chocolatePasteOrders = Set.of("35665");
        assertEquals(chocolatePasteOrders, getSetOfString(chocolatePasteList));
        assertEquals(Set.of("3434 Паста шоколадна"), getProductNamesSet(chocolatePasteList));

        List<Pair<String, ProductDto>> muffinList = map.remove("678968 Суміш для випікання Мрія Маффіни з апельсиновою цедрою");
        assertEquals(1, chocolatePasteList.size());
        Set<String> muffinOrders = Set.of("35665", "36325");
        assertEquals(muffinOrders, getSetOfString(muffinList));
        assertEquals(Set.of("678968 Суміш для випікання Мрія Маффіни з апельсиновою цедрою"), getProductNamesSet(muffinList));

        assertEquals(0, map.size());
    }

    @Test
    public void testGroupByOrders() {
        Map<String, List<Pair<String, ProductDto>>> map = carLoadSheetGenerator.groupByOrders(carLoadDto);

        List<Pair<String, ProductDto>> order_35665 = map.remove("35665");
        assertEquals(3, order_35665.size());
        Set<String> products_order_35665 = Set.of("3434 Паста шоколадна", "678968 Суміш для випікання Мрія Маффіни з апельсиновою цедрою");
        assertEquals(products_order_35665, getSetOfString(order_35665));
        assertEquals(products_order_35665, getProductNamesSet(order_35665));

        List<Pair<String, ProductDto>> order_36325 = map.remove("36325");
        assertEquals(1, order_36325.size());
        Set<String> products_order_36325 = Set.of("678968 Суміш для випікання Мрія Маффіни з апельсиновою цедрою");
        assertEquals(products_order_36325, getSetOfString(order_36325));
        assertEquals(products_order_36325, getProductNamesSet(order_36325));

        assertEquals(0, map.size());
    }

    @Test
    public void testGetAllProductAmount() {
        List<Pair<String, ProductDto>> list = List.of(
                Pair.of("35665", productSecond),
                Pair.of("36325", productThird)
        );
        int allProductAmount = carLoadSheetGenerator.getAllProductAmount(list);

        assertEquals(15, allProductAmount);
    }

    private Set<String> getSetOfString(List<Pair<String, ProductDto>> list) {
        return list.stream()
                .map(Pair::getFirst)
                .collect(Collectors.toSet());
    }

    private Set<String> getProductNamesSet(List<Pair<String, ProductDto>> list) {
        return list.stream()
                .map(Pair::getSecond)
                .map(ProductDto::getProductName)
                .collect(Collectors.toSet());
    }
}