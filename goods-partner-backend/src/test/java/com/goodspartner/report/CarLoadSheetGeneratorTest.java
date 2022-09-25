package com.goodspartner.report;

import com.goodspartner.dto.Product;
import com.goodspartner.entity.Car;
import com.goodspartner.entity.CarLoad;
import com.goodspartner.entity.Delivery;
import com.goodspartner.entity.OrderExternal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.util.Pair;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CarLoadSheetGeneratorTest {

    private final CarLoadSheetGenerator carLoadSheetGenerator = new CarLoadSheetGenerator();

    private CarLoad carLoad;
    private Product productSecond;
    private Product productThird;

    @BeforeEach
    public void setUp() {
        Delivery delivery = new Delivery();
        delivery.setId(UUID.randomUUID());

        Car car = new Car(
                1,
                "Mercedes Vito",
                "Ivan Piddubny",
                true,
                false,
                "AA 2222 CT",
                1000,
                10);

        Product productFirst = Product.builder()
                .productName("3434 Паста шоколадна")
                .amount(1)
                .unitWeight(1.52)
                .totalProductWeight(1.52)
                .build();

        productSecond = Product.builder()
                .productName("678968 Суміш для випікання Мрія Маффіни з апельсиновою цедрою")
                .amount(5)
                .unitWeight(57.8)
                .totalProductWeight(289)
                .build();

        productThird = Product.builder()
                .productName("678968 Суміш для випікання Мрія Маффіни з апельсиновою цедрою")
                .amount(10)
                .unitWeight(47.8)
                .totalProductWeight(478)
                .build();

        OrderExternal orderFirst = new OrderExternal();
        orderFirst.setProducts(List.of(productFirst, productSecond, productThird));
        orderFirst.setOrderNumber(String.valueOf(35665));
        orderFirst.setOrderWeight(579.52);
        orderFirst.setId(1);

        OrderExternal orderSecond = new OrderExternal();
        orderSecond.setProducts(List.of(productThird));
        orderSecond.setOrderNumber(String.valueOf(36325));
        orderSecond.setOrderWeight(1.52);
        orderSecond.setId(2);

        carLoad = new CarLoad();
        carLoad.setCar(car);
        carLoad.setOrders(Arrays.asList(orderFirst, orderSecond));
        carLoad.setDelivery(delivery);
    }

    @Test
    void testGroupByProducts() {
        Map<String, List<Pair<String, Product>>> map = carLoadSheetGenerator.groupByProducts(carLoad);

        List<Pair<String, Product>> chocolatePasteList = map.remove("3434 Паста шоколадна");
        assertEquals(1, chocolatePasteList.size());
        Set<String> chocolatePasteOrders = Set.of("35665");
        assertEquals(chocolatePasteOrders, getSetOfString(chocolatePasteList));
        assertEquals(Set.of("3434 Паста шоколадна"), getProductNamesSet(chocolatePasteList));

        List<Pair<String, Product>> muffinList = map.remove("678968 Суміш для випікання Мрія Маффіни з апельсиновою цедрою");
        assertEquals(1, chocolatePasteList.size());
        Set<String> muffinOrders = Set.of("35665", "36325");
        assertEquals(muffinOrders, getSetOfString(muffinList));
        assertEquals(Set.of("678968 Суміш для випікання Мрія Маффіни з апельсиновою цедрою"), getProductNamesSet(muffinList));

        assertEquals(0, map.size());
    }

    @Test
    void testGroupByOrders() {
        Map<String, List<Pair<String, Product>>> map = carLoadSheetGenerator.groupByOrders(carLoad);

        List<Pair<String, Product>> order_35665 = map.remove("35665");
        assertEquals(3, order_35665.size());
        Set<String> products_order_35665 = Set.of("3434 Паста шоколадна", "678968 Суміш для випікання Мрія Маффіни з апельсиновою цедрою");
        assertEquals(products_order_35665, getSetOfString(order_35665));
        assertEquals(products_order_35665, getProductNamesSet(order_35665));

        List<Pair<String, Product>> order_36325 = map.remove("36325");
        assertEquals(1, order_36325.size());
        Set<String> products_order_36325 = Set.of("678968 Суміш для випікання Мрія Маффіни з апельсиновою цедрою");
        assertEquals(products_order_36325, getSetOfString(order_36325));
        assertEquals(products_order_36325, getProductNamesSet(order_36325));

        assertEquals(0, map.size());
    }

    @Test
    void testGetAllProductAmount() {
        List<Pair<String, Product>> list = List.of(
                Pair.of("35665", productSecond),
                Pair.of("36325", productThird)
        );
        int allProductAmount = carLoadSheetGenerator.getAllProductAmount(list);

        assertEquals(15, allProductAmount);
    }

    private Set<String> getSetOfString(List<Pair<String, Product>> list) {
        return list.stream()
                .map(Pair::getFirst)
                .collect(Collectors.toSet());
    }

    private Set<String> getProductNamesSet(List<Pair<String, Product>> list) {
        return list.stream()
                .map(Pair::getSecond)
                .map(Product::getProductName)
                .collect(Collectors.toSet());
    }
}