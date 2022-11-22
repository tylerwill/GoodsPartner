package com.goodspartner.service.impl;

import com.github.database.rider.core.api.dataset.DataSet;
import com.github.database.rider.spring.api.DBRider;
import com.goodspartner.AbstractBaseITest;
import com.goodspartner.entity.DeliveryHistory;
import com.goodspartner.exception.DeliveryNotFoundException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DBRider
class DefaultDeliveryHistoryServiceITest extends AbstractBaseITest {

    @Autowired
    private DefaultDeliveryHistoryService deliveryHistoryService;

    @Test
    @DataSet(value = "datasets/history/delivery-history-test.yml", disableConstraints = true,
            cleanAfter = true, cleanBefore = true, skipCleaningFor = "flyway_schema_history")
    @DisplayName("when Find By Delivery Id then Expected List Of Delivery History Dto Returned")
    public void whenFindByDeliveryId_thenDeliveryHistoryDtoReturned() {

        // Given
        LocalDateTime expectedDateTime = LocalDateTime.of(2022, 11, 5, 23, 10, 58);
        UUID expectedDeliveryId = UUID.fromString("00000000-0000-0000-0000-000000000123");
        UUID expectedDeliveryHistoryId = UUID.fromString("00000000-0000-0000-0000-000000000999");

        // When
        List<DeliveryHistory> deliveryHistoryDtoList = deliveryHistoryService.findByDeliveryId(expectedDeliveryId);
        DeliveryHistory deliveryHistoryDto = deliveryHistoryDtoList.get(0);

        // Then
        assertEquals(expectedDeliveryHistoryId, deliveryHistoryDto.getId());
        assertEquals(expectedDeliveryId, deliveryHistoryDto.getDelivery().getId());
        assertEquals((expectedDateTime), deliveryHistoryDto.getCreatedAt());
        assertEquals(("ROLE_ANONYMOUS"), deliveryHistoryDto.getRole());
        assertEquals(("anonymous@mail"), deliveryHistoryDto.getUserEmail());
        assertEquals(("Анонім Anonymous створив(ла) доставку"), deliveryHistoryDto.getAction());

    }

    @Test
    @DataSet(disableConstraints = true,
            cleanAfter = true, cleanBefore = true, skipCleaningFor = "flyway_schema_history")
    @DisplayName("when Find By Delivery Id Throw DeliveryNotFoundException If Delivery Id Not Exist")
    public void whenFindByDeliveryId_thenThrowDeliveryNotFoundException() {

        UUID notExistUuid = UUID.fromString("00000000-0000-0000-0000-000000000000");

        Assertions.assertThrows(DeliveryNotFoundException.class,
                () -> deliveryHistoryService.findByDeliveryId(notExistUuid));
    }

}