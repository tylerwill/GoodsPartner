package com.goodspartner.mapper;

import com.goodspartner.dto.DeliveryDto;
import com.goodspartner.entity.Delivery;
import com.goodspartner.entity.DeliveryStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(classes = {
        DeliveryMapperImpl.class,
        OrderExternalMapperImpl.class,
        CarLoadMapperImpl.class,
        CarMapperImpl.class,
        OrderExternalMapperImpl.class,
        RouteMapperImpl.class,
        ProductShippingMapper.class,
        StoreMapperImpl.class
})
public class DeliveryMapperTest {

    @Autowired
    private DeliveryMapper deliveryMapper;

    @Test
    @DisplayName("when Map DeliveryDto then Return Delivery")
    void whenMapDeliveryDto_thenReturnDelivery() {

        DeliveryDto deliveryDto = DeliveryDto.builder()
                .id(UUID.fromString("2221111-3344-5555-0000-444466669999"))
                .deliveryDate(LocalDate.of(2022, 8, 15))
                .routes(null)
                .orders(null)
                .productsShipping(null)
                .status(DeliveryStatus.COMPLETED)
                .build();

        Delivery mappedDelivery = deliveryMapper.deliveryDtoToDelivery(deliveryDto);

        assertEquals(UUID.fromString("2221111-3344-5555-0000-444466669999"), mappedDelivery.getId());
        assertEquals(LocalDate.of(2022, 8, 15), mappedDelivery.getDeliveryDate());

        assertTrue(mappedDelivery.getRoutes().isEmpty());
        assertTrue(mappedDelivery.getOrders().isEmpty());
        assertTrue(mappedDelivery.getCarLoads().isEmpty());
        assertEquals(DeliveryStatus.COMPLETED, mappedDelivery.getStatus());
    }

}
