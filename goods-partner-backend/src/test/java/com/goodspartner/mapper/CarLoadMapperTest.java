package com.goodspartner.mapper;

import com.goodspartner.dto.CarDto;
import com.goodspartner.dto.CarLoadDto;
import com.goodspartner.dto.MapPoint;
import com.goodspartner.dto.OrderDto;
import com.goodspartner.dto.Product;
import com.goodspartner.entity.AddressStatus;
import com.goodspartner.entity.Car;
import com.goodspartner.entity.CarLoad;
import com.goodspartner.entity.OrderExternal;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

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
                .status(AddressStatus.KNOWN)
                .address("м.Київ, Марії Лагунової, 11")
                .longitude(53.0099)
                .latitude(35.0099)
                .build();

        Product product = Product.builder()
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
                .shippingDate(LocalDate.of(2022, 2, 17))
                .comment("Urgent")
                .managerFullName("Georg")
                .clientName("ABS")
                .address("Бровари, Марії Лагунової, 11")
                .mapPoint(mapPoint)
                .products(List.of(product))
                .orderWeight(1340.0)
                .deliveryId(UUID.fromString("237e9877-e79b-12d4-a765-321741963000"))
                .build();

        // TOOD builder
        CarDto carDto = new CarDto(
                1,
                "FORD",
                "12345",
                "Oleg",
                2000,
                false,
                true,
                26
        );

        CarLoadDto carLoadDto = new CarLoadDto(carDto, List.of(orderDto));

        CarLoad carLoad = carLoadMapper.mapDtoToEntity(carLoadDto);

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

        List<OrderExternal> mappedOrderExternals = orderExternalMapper.mapToEntities(carLoadDto.getOrders());
        assertEquals(2, mappedOrderExternals.get(0).getId());
        assertEquals("1232", mappedOrderExternals.get(0).getOrderNumber());
        assertEquals(LocalDate.of(2022, 2, 17), mappedOrderExternals.get(0).getShippingDate());
        assertEquals("ABS", mappedOrderExternals.get(0).getAddressExternal().getOrderAddressId().getClientName());
        assertEquals("Бровари, Марії Лагунової, 11", mappedOrderExternals.get(0).getAddressExternal().getOrderAddressId().getOrderAddress());
        assertEquals("Georg", mappedOrderExternals.get(0).getManagerFullName());
        assertEquals(1340.0, mappedOrderExternals.get(0).getOrderWeight());
        assertEquals(List.of(product), mappedOrderExternals.get(0).getProducts());
        assertEquals(UUID.fromString("237e9877-e79b-12d4-a765-321741963000"), mappedOrderExternals.get(0).getDelivery().getId());

        Assertions.assertNull(carLoad.getDelivery());
    }
}
