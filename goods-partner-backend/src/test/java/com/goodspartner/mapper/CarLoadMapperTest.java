package com.goodspartner.mapper;

import com.goodspartner.dto.CarDto;
import com.goodspartner.dto.OrderDto;
import com.goodspartner.dto.ProductDto;
import com.goodspartner.entity.Car;
import com.goodspartner.entity.CarLoad;
import com.goodspartner.entity.OrderExternal;
import com.goodspartner.web.controller.response.RoutesCalculation;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class CarLoadMapperTest {

    private final CarLoadMapper carLoadMapper = Mappers.getMapper(CarLoadMapper.class);
    private final CarMapper carMapper = Mappers.getMapper(CarMapper.class);
    private final OrderExternalMapper orderExternalMapper = Mappers.getMapper(OrderExternalMapper.class);

    @Test
    @DisplayName("when Map CarLoadDto then Return CarLoad")
    void whenMapCarLoadDto_thenReturnCarLoad() {

        ProductDto productDto = ProductDto.builder()
                .amount(1)
                .storeName("Склад №1")
                .unitWeight(12.00)
                .productName("Наповнювач фруктово-ягідний (декоргель) (12 кг)")
                .totalProductWeight(12.00)
                .build();

        OrderDto orderDto = OrderDto.builder()
                .id(2)
                .refKey("2221111-3344-5555-0000-444466669999")
                .orderNumber("1232")
                .createdDate(LocalDate.of(2022, 2, 17))
                .comment("Urgent")
                .managerFullName("Georg")
                .clientName("ABS")
                .address("Бровари, Марії Лагунової, 11")
                .mapPoint(null)
                .products(List.of(productDto))
                .orderWeight(1340.0)
                .build();

        CarDto carDto = CarDto.builder()
                .id(1)
                .name("FORD")
                .licencePlate("12345")
                .driver("Oleg")
                .weightCapacity(2000)
                .cooler(false)
                .available(true)
                .loadSize(15.5)
                .travelCost(26)
                .build();

        RoutesCalculation.CarLoadDto carLoadDto = RoutesCalculation.CarLoadDto.builder()
                .car(carDto)
                .orders(List.of(orderDto))
                .build();

        CarLoad carLoad = carLoadMapper.carLoadDtoToCarLoad(carLoadDto);

        Assertions.assertNull(carLoad.getId());

        Car mappedCar = carMapper.carDtoToCar(carDto);
        assertEquals(1, mappedCar.getId());
        assertEquals("FORD", mappedCar.getName());
        assertEquals("12345", mappedCar.getLicencePlate());
        assertEquals("Oleg", mappedCar.getDriver());
        assertEquals(2000, mappedCar.getWeightCapacity());
        assertFalse(mappedCar.isCooler());
        assertTrue(mappedCar.isAvailable());
        assertEquals(26, mappedCar.getTravelCost());

        List<OrderExternal> mappedOrderExternals = orderExternalMapper.mapOrderDtosToOrderExternal(carLoadDto.getOrders());
        assertEquals(2, mappedOrderExternals.get(0).getId());
        assertEquals("1232", mappedOrderExternals.get(0).getOrderNumber());
        assertEquals(LocalDate.of(2022, 2, 17), mappedOrderExternals.get(0).getCreatedDate());
        assertEquals("ABS", mappedOrderExternals.get(0).getClientName());
        assertEquals("Бровари, Марії Лагунової, 11", mappedOrderExternals.get(0).getAddress());
        assertEquals("Georg", mappedOrderExternals.get(0).getManagerFullName());
        assertEquals(1340.0, mappedOrderExternals.get(0).getOrderWeight());
        assertFalse(mappedOrderExternals.get(0).isValidAddress());
        assertEquals(List.of(productDto), mappedOrderExternals.get(0).getProducts());
        assertNull(mappedOrderExternals.get(0).getDelivery());

        Assertions.assertNull(carLoad.getDelivery());
    }
}
