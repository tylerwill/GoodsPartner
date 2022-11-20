package com.goodspartner.service;

import com.goodspartner.dto.OrderDto;
import com.goodspartner.entity.Delivery;
import com.goodspartner.entity.OrderExternal;
import com.goodspartner.web.controller.request.RemoveOrdersRequest;
import com.goodspartner.web.controller.request.RescheduleOrdersRequest;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;

import java.util.List;
import java.util.UUID;

public interface OrderExternalService {

    /* --- Data Fetching ---*/

    List<OrderExternal> getByDeliveryId(UUID deliveryId, OAuth2AuthenticationToken authentication);

    List<OrderExternal> getSkippedOrders();

    List<OrderExternal> getCompletedOrders();

    List<OrderExternal> getScheduledOrders();

    /* --- Data Modification ---*/

    List<OrderExternal> rescheduleSkippedOrders(RescheduleOrdersRequest rescheduleOrdersRequest);

    List<OrderExternal> removeExcludedOrders(RemoveOrdersRequest removeOrdersRequest);

    OrderExternal update(int id, OrderDto orderDto);

    void saveOrdersForDelivery(Delivery delivery);
}