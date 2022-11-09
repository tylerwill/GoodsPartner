package com.goodspartner.service.impl;

import com.github.database.rider.core.api.dataset.DataSet;
import com.github.database.rider.spring.api.DBRider;
import com.goodspartner.AbstractBaseITest;
import com.goodspartner.action.OrderAction;
import com.goodspartner.dto.OrderDto;
import com.goodspartner.dto.UpdateDto;
import com.goodspartner.entity.OrderExternal;
import com.goodspartner.repository.OrderExternalRepository;
import com.goodspartner.service.OrderExternalService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DBRider
class DefaultOrderExternalServiceTest extends AbstractBaseITest {

    @Autowired
    private OrderExternalService orderExternalService;

    @Autowired
    private OrderExternalRepository orderExternalRepository;

    @Test
    @DataSet(value = "response/order-controller-filter.json",
            cleanAfter = true, cleanBefore = true, skipCleaningFor = "flyway_schema_history")
    @DisplayName("Get excluded and dropped orders")
    void getExcludedDroppedOrders() {
        List<OrderDto> skippedOrders = orderExternalService.getFilteredOrders(true, true);
        assertEquals(1, skippedOrders.size());
    }

    @Test
    @DataSet(value = "response/order-controller-filter.json",
            cleanAfter = true, cleanBefore = true, skipCleaningFor = "flyway_schema_history")
    @DisplayName("Get only excluded orders")
    void getOnlyExcludedOrders() {
        List<OrderDto> skippedOrders = orderExternalService.getFilteredOrders(true, false);
        assertEquals(1, skippedOrders.size());
    }

    @Test
    @DataSet(value = "response/order-controller-filter.json",
            cleanAfter = true, cleanBefore = true, skipCleaningFor = "flyway_schema_history")
    @DisplayName("Get only dropped orders")
    void getOnlyDroppedOrders() {
        List<OrderDto> skippedOrders = orderExternalService.getFilteredOrders(false, true);
        assertEquals(1, skippedOrders.size());
    }

    @Test
    @DataSet(value = "response/order-controller-filter.json",
            cleanAfter = true, cleanBefore = true, skipCleaningFor = "flyway_schema_history")
    @DisplayName("Get not excluded or dropped orders")
    void getNotExcludedOrDroppedOrders() {
        List<OrderDto> skippedOrders = orderExternalService.getFilteredOrders(false, false);
        assertEquals(1, skippedOrders.size());
    }

    @Test
    @DataSet(value = "response/order-controller-filter.json",
            cleanAfter = true, cleanBefore = true, skipCleaningFor = "flyway_schema_history")
    @DisplayName("Update delivery date")
    void updateDeliveryDate() {

        UpdateDto updateDto = new UpdateDto();
        List<Integer> list = List.of(251);
        LocalDate date = LocalDate.of(2022, 2, 20);
        updateDto.setDeliveryDate(date);
        updateDto.setOrdersIdList(list);

        List<OrderDto> skippedOrders = orderExternalService.getFilteredOrders(false, false);
        assertEquals(1, skippedOrders.size());
        OrderDto orderDto = skippedOrders.get(0);

        assertEquals(UUID.fromString("70574dfd-48a3-40c7-8b0c-3e5defe7d080"), orderDto.getDeliveryId());
        assertEquals(251, orderDto.getId());

        orderExternalService.updateDeliveryDate(updateDto, OrderAction.of("schedule"));

        Optional<OrderExternal> orderExternal = orderExternalRepository.findById(251);

        assertEquals(UUID.fromString("70574dfd-48a3-40c7-8b0c-3e5defe7d082"), orderExternal.get().getDelivery().getId());
        assertEquals(251, orderExternal.get().getId());
        assertEquals(LocalDate.of(2022, 2, 20), orderExternal.get().getDelivery().getDeliveryDate());

    }

}