package com.goodspartner.service.impl;

import com.github.database.rider.core.api.dataset.DataSet;
import com.github.database.rider.core.api.dataset.ExpectedDataSet;
import com.github.database.rider.spring.api.DBRider;
import com.goodspartner.AbstractBaseITest;
import com.goodspartner.dto.DeliveryDto;
import com.goodspartner.entity.Delivery;
import com.goodspartner.entity.DeliveryStatus;
import com.goodspartner.entity.OrderExternal;
import com.goodspartner.exception.DeliveryModifyException;
import com.goodspartner.mapper.DeliveryMapper;
import com.goodspartner.repository.DeliveryRepository;
import com.goodspartner.service.CarLoadService;
import com.goodspartner.service.DeliveryHistoryService;
import com.goodspartner.service.DeliveryService;
import com.goodspartner.service.RouteCalculationService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

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

    @Test
    @DisplayName("when Calculate Delivery then delivery fetched by id, orders loaded and analyzed for excluded parameter")
    void testCalculateDelivery_WithExcludedParameter() {

        //add mocks to construct mock Delivery object
        DeliveryRepository deliveryRepositoryMock = mock(DeliveryRepository.class);
        RouteCalculationService routeCalculationServiceMock = mock(RouteCalculationService.class);
        CarLoadService carLoadServiceMock = mock(CarLoadService.class);
        DeliveryHistoryService deliveryHistoryServiceMock = mock(DeliveryHistoryService.class);
        DeliveryMapper deliveryMapperMock = mock(DeliveryMapper.class);

        //construct mock Delivery object
        DeliveryService mockService =
                new DefaultDeliveryService(deliveryMapperMock, deliveryRepositoryMock,
                        routeCalculationServiceMock, carLoadServiceMock, deliveryHistoryServiceMock);

        //configure mock objects
        Delivery mockDelivery = mock(Delivery.class);
        List<OrderExternal> includedListMock = mock(List.class);

        when(deliveryRepositoryMock.findById(any(UUID.class))).thenReturn(Optional.ofNullable(mockDelivery));
        when(Objects.requireNonNull(mockDelivery).getStatus()).thenReturn(DeliveryStatus.DRAFT);
        when(mockDelivery.getOrders()).thenReturn(includedListMock);
        when(includedListMock.isEmpty()).thenReturn(false);

        //call tested method
        mockService.calculateDelivery(UUID.fromString("11111111-2222-2222-2222-333333333333"));

        //verify invocation
        verify(includedListMock).stream();
    }
}