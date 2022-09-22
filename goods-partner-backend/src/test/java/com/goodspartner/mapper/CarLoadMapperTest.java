package com.goodspartner.mapper;

import com.goodspartner.dto.CarDto;
import com.goodspartner.dto.MapPoint;
import com.goodspartner.dto.OrderDto;
import com.goodspartner.dto.ProductDto;
import com.goodspartner.entity.Car;
import com.goodspartner.entity.CarLoad;
import com.goodspartner.entity.OrderExternal;
import com.goodspartner.web.controller.response.RoutesCalculation;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = {CarLoadMapperImpl.class, OrderExternalMapperImpl.class, CarMapperImpl.class, OrderExternalMapperImpl.class})
public class CarLoadMapperTest {

    @Autowired
    private CarLoadMapper carLoadMapper;
    @Autowired
    private CarMapper carMapper;
    @Autowired
    private OrderExternalMapper orderExternalMapper;

    @Test
    @DisplayName("when Map CarLoadDto then Return CarLoad")
    void whenMapCarLoadDto_thenReturnCarLoad() {
        MapPoint mapPoint = MapPoint.builder()
                .status(MapPoint.AddressStatus.KNOWN)
                .address("м.Київ, Марії Лагунової, 11")
                .longitude(53.0099)
                .latitude(35.0099)
                .build();

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
                .mapPoint(mapPoint)
                .products(List.of(productDto))
                .orderWeight(1340.0)
                .build();

        CarDto carDto = new CarDto(
                1,
                "FORD",
                "12345",
                "Oleg",
                2000,
                false,
                true,
                15.5,
                26
        );

        RoutesCalculation.CarLoadDto carLoadDto = new RoutesCalculation.CarLoadDto(carDto, List.of(orderDto));

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
        assertEquals("ABS", mappedOrderExternals.get(0).getAddressExternal().getOrderAddressId().getClientName());
        assertEquals("Бровари, Марії Лагунової, 11", mappedOrderExternals.get(0).getAddressExternal().getOrderAddressId().getOrderAddress());
        assertEquals("Georg", mappedOrderExternals.get(0).getManagerFullName());
        assertEquals(1340.0, mappedOrderExternals.get(0).getOrderWeight());
        assertFalse(mappedOrderExternals.get(0).isValidAddress());
        assertEquals(List.of(productDto), mappedOrderExternals.get(0).getProducts());
        assertNull(mappedOrderExternals.get(0).getDelivery().getId());

        Assertions.assertNull(carLoad.getDelivery());
    }
}
