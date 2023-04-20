package com.goodspartner.impl;

import com.github.database.rider.core.api.dataset.DataSet;
import com.github.database.rider.spring.api.DBRider;
import com.goodspartner.AbstractBaseITest;
import com.goodspartner.dto.OrderDto;
import com.goodspartner.entity.Delivery;
import com.goodspartner.entity.OrderExternal;
import com.goodspartner.facade.DeliveryFacade;
import com.goodspartner.facade.OrderFacade;
import com.goodspartner.repository.DeliveryRepository;
import com.goodspartner.repository.OrderExternalRepository;
import com.goodspartner.service.IntegrationService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Sort;
import org.testcontainers.shaded.org.awaitility.Durations;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.mockito.Mockito.when;
import static org.testcontainers.shaded.org.awaitility.Awaitility.await;

@Slf4j
@DBRider
class DefaultDeliveryFacadeITest extends AbstractBaseITest {

    private static final String INTEGRATION_SERVICE_KNOWN_ORDERS_MOCK_PATH = "mock/integration/mock-integration-service-return-known-orders.json";
    private static final LocalDate SHIPPING_DATE = LocalDate.parse("2023-04-06");

    @Autowired
    private DeliveryFacade deliveryFacade;
    @Autowired
    private OrderExternalRepository orderExternalRepository;
    @Autowired
    private DeliveryRepository deliveryRepository;
    @Autowired
    private OrderFacade orderFacade;
    @MockBean
    private IntegrationService integrationService;

    @Test
    @DataSet(value = "datasets/delivery/sync/synchronize_delivery_orders.json",
            cleanAfter = true, cleanBefore = true, skipCleaningFor = "flyway_schema_history")
    @DisplayName("validate Queries After Add Car")
    void whenSyncThenReturnOnlyRescheduledOrders() {

        mockIntegrationService(INTEGRATION_SERVICE_KNOWN_ORDERS_MOCK_PATH);

        assertEquals(3, orderExternalRepository
                .findByDeliveryId(UUID.fromString("689682b6-38b1-4b22-ba18-1d35cdc3ece5"), Sort.unsorted()).size());

        assertEquals(5, orderExternalRepository
                .findByDeliveryId(UUID.fromString("8e06b64b-2f6e-4255-a5b0-cde216d4ba46"), Sort.unsorted()).size());

        assertEquals(8, orderExternalRepository.findAll().size());

        deliveryFacade.resyncOrders(UUID.fromString("8e06b64b-2f6e-4255-a5b0-cde216d4ba46"));

        await()
                .atLeast(Durations.TWO_HUNDRED_MILLISECONDS)
                .atMost(Durations.TWO_SECONDS)
                .with()
                .pollInterval(Durations.TWO_HUNDRED_MILLISECONDS)
                .until(() -> orderExternalRepository
                        .findByDeliveryId(UUID.fromString("8e06b64b-2f6e-4255-a5b0-cde216d4ba46"), Sort.unsorted())
                        .size() == 3); // 2 rescheduled + 1 fetched instead of existing 3

        assertEquals(3, orderExternalRepository
                .findByDeliveryId(UUID.fromString("689682b6-38b1-4b22-ba18-1d35cdc3ece5"), Sort.unsorted()).size());

        assertEquals(6, orderExternalRepository.findAll().size());
    }

    @Test
    @DataSet(value = "datasets/delivery/sync/synchronize_delivery_orders.json",
            cleanAfter = true, cleanBefore = true, skipCleaningFor = "flyway_schema_history")
    @DisplayName("validate Queries After Add Car")
    void whenRescheduledOrdersSaved() {
        List<OrderExternal> orderExternals = orderExternalRepository
                .findByDeliveryId(UUID.fromString("689682b6-38b1-4b22-ba18-1d35cdc3ece5"), Sort.unsorted());
        assertEquals(3, orderExternals.size());
        List<OrderExternal> nextDaydeliveryorderExternals = orderExternalRepository
                .findByDeliveryId(UUID.fromString("8e06b64b-2f6e-4255-a5b0-cde216d4ba46"), Sort.unsorted());
        assertEquals(5, nextDaydeliveryorderExternals.size());


        deliveryFacade.resyncOrders(UUID.fromString("8e06b64b-2f6e-4255-a5b0-cde216d4ba46"));
        await()
                .atLeast(Durations.TWO_HUNDRED_MILLISECONDS)
                .atMost(Durations.TWO_SECONDS)
                .with()
                .pollInterval(Durations.TWO_HUNDRED_MILLISECONDS)
                .until(() -> deliveryRepository.findByIdWithOrders(UUID.fromString("8e06b64b-2f6e-4255-a5b0-cde216d4ba46"))
                        .map(Delivery::getOrders)
                        .map(List::size)
                        .map(size -> size == 2)
                        .orElse(false));

        Delivery deliverySynced = deliveryRepository.findByIdWithOrders(UUID.fromString("8e06b64b-2f6e-4255-a5b0-cde216d4ba46")).get();

        OrderExternal orderToReschedule = orderExternals.stream()
                .filter(e -> "00000004983".equals(e.getOrderNumber())).findFirst().get();
        OrderExternal rescheduledOrder = deliverySynced.getOrders().stream()
                .filter(e -> "00000004983".equals(e.getOrderNumber())).findFirst().get();

        assertEquals(orderToReschedule.getOrderNumber(), rescheduledOrder.getOrderNumber());
        assertEquals(orderToReschedule.getRescheduleDate(), rescheduledOrder.getShippingDate());
        assertNotEquals(orderToReschedule.getId(), rescheduledOrder.getId());
        assertNotEquals(orderToReschedule.getDelivery(), rescheduledOrder.getDelivery());
    }

    private void mockIntegrationService(String mockPath) {
        when(integrationService.findAllByShippingDate(SHIPPING_DATE))
                .thenReturn(getMockedListObjects(mockPath, OrderDto.class));
    }

}