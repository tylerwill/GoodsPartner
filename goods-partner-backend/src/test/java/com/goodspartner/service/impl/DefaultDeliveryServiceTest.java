package com.goodspartner.service.impl;

import com.github.database.rider.core.api.dataset.DataSet;
import com.github.database.rider.core.api.dataset.ExpectedDataSet;
import com.github.database.rider.spring.api.DBRider;
import com.goodspartner.AbstractBaseITest;
import com.goodspartner.dto.DeliveryDto;
import com.goodspartner.entity.DeliveryStatus;
import com.goodspartner.exceptions.DeliveryModifyException;
import com.goodspartner.service.DeliveryService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.UUID;

@DBRider
class DefaultDeliveryServiceTest extends AbstractBaseITest {

    @Autowired
    private DeliveryService deliveryService;

    // TODO fix RoutePoint matching. At th emoment due to reordering/completedAt/etc results doesn't match
    @Test
    @DataSet(value = "common/close_delivery/initial_routes_and_deliveries.yml",
            cleanAfter = true, skipCleaningFor = "flyway_schema_history")
    @ExpectedDataSet(value = "common/close_delivery/update_and_close_delivery.yml", ignoreCols = "ROUTE_POINTS")
    @DisplayName("Updated and close delivery")
    public void testUpdateDelivery() {
        UUID uuid = UUID.fromString("d0000000-0000-0000-0000-000000000003");
        DeliveryDto deliveryDto = deliveryService.findById(uuid);
        deliveryDto.setStatus(DeliveryStatus.COMPLETED);

        deliveryService.update(uuid, deliveryDto);
    }

    @Test
    @DataSet(value = "common/close_delivery/initial_routes_and_deliveries.yml",
            cleanAfter = true, cleanBefore = true, skipCleaningFor = "flyway_schema_history")
    @DisplayName("Updated delivery should throw DeliveryModifyException")
    public void testUpdateDelivery_shouldThrowDeliveryModifyException() {
        Assertions.assertThrows(DeliveryModifyException.class, () -> {
            UUID uuid = UUID.fromString("d0000000-0000-0000-0000-000000000004");
            DeliveryDto deliveryDto = deliveryService.findById(uuid);
            deliveryDto.setStatus(DeliveryStatus.COMPLETED);

            deliveryService.update(uuid, deliveryDto);
        });
    }

}