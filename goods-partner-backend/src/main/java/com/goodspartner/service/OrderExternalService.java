package com.goodspartner.service;

import com.goodspartner.dto.OrderDto;
import com.goodspartner.entity.Delivery;
import com.goodspartner.entity.OrderExternal;
import com.goodspartner.web.controller.request.ExcludeOrderRequest;
import com.goodspartner.web.controller.request.RemoveOrdersRequest;
import com.goodspartner.web.controller.request.RescheduleOrdersRequest;

import java.util.List;
import java.util.UUID;

public interface OrderExternalService {

    /* --- Data Fetching ---*/

    List<OrderExternal> getByDeliveryId(UUID deliveryId);

    List<OrderExternal> getSkippedOrders();

    List<OrderExternal> getCompletedOrders();

    List<OrderExternal> getScheduledOrders();

    /* --- Data Modification ---*/

    List<OrderExternal> rescheduleSkippedOrders(RescheduleOrdersRequest rescheduleOrdersRequest);

    List<OrderExternal> removeExcludedOrders(RemoveOrdersRequest removeOrdersRequest);

    OrderExternal update(long id, OrderDto orderDto);

    void bindExternalOrdersWithDelivery(List<OrderExternal> externalOrders, UUID deliveryId);

    void checkDeliveryReadiness(Delivery delivery);

    OrderExternal excludeOrder(long id, ExcludeOrderRequest excludeOrderRequest);

    List<OrderExternal> getInvalidOrdersForCalculation(UUID deliveryId);

    void cleanupOrders(UUID deliveryId);

}