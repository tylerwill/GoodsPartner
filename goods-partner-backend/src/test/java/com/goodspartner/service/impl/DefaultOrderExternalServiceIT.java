package com.goodspartner.service.impl;

import com.github.database.rider.core.api.dataset.DataSet;
import com.github.database.rider.spring.api.DBRider;
import com.goodspartner.AbstractBaseITest;
import com.goodspartner.entity.OrderExternal;
import com.goodspartner.repository.OrderExternalRepository;
import com.goodspartner.service.OrderExternalService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DBRider
class DefaultOrderExternalServiceIT extends AbstractBaseITest {

    @Autowired
    private OrderExternalService orderExternalService;

    @Autowired
    private OrderExternalRepository orderExternalRepository;

    @Test
    @DataSet(value = "datasets/orders/order-controller-filter.json",
            cleanAfter = true, cleanBefore = true, skipCleaningFor = "flyway_schema_history")
    @DisplayName("Get only excluded / dropped / skipped orders")
    void getExcludedDroppedOrders() {
        List<OrderExternal> skippedOrders = orderExternalService.getSkippedOrders();
        assertEquals(2, skippedOrders.size());
    }

    @Test
    @DataSet(value = "datasets/orders/order-controller-filter.json",
            cleanAfter = true, cleanBefore = true, skipCleaningFor = "flyway_schema_history")
    @DisplayName("Get only completed orders")
    void getOnlyExcludedOrders() {
        List<OrderExternal> completedOrders = orderExternalService.getCompletedOrders();
        assertEquals(1, completedOrders.size());
    }
}