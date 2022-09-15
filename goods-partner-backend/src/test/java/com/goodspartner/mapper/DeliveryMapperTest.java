package com.goodspartner.mapper;

import com.goodspartner.dto.DeliveryDto;
import com.goodspartner.entity.Delivery;
import com.goodspartner.entity.DeliveryStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.time.LocalDate;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class DeliveryMapperTest {

    private final DeliveryMapper deliveryMapper = Mappers.getMapper(DeliveryMapper.class);

    @Test
    @DisplayName("when Map DeliveryDto then Return Delivery")
    void whenMapDeliveryDto_thenReturnDelivery() {

        DeliveryDto deliveryDto = DeliveryDto.builder()
                .id(UUID.fromString("2221111-3344-5555-0000-444466669999"))
                .deliveryDate(LocalDate.of(2022, 8, 15))
                .routes(null)
                .orders(null)
                .carLoads(null)
                .status(DeliveryStatus.COMPLETED)
                .build();

        Delivery mappedDelivery = deliveryMapper.deliveryDtoToDelivery(deliveryDto);

        assertEquals(UUID.fromString("2221111-3344-5555-0000-444466669999"), mappedDelivery.getId());
        assertEquals(LocalDate.of(2022, 8, 15), mappedDelivery.getDeliveryDate());
        assertNull(mappedDelivery.getRoutes());
        assertNull(mappedDelivery.getOrders());
        assertNull(mappedDelivery.getCarLoads());
        assertEquals(DeliveryStatus.COMPLETED, mappedDelivery.getStatus());
    }

}
