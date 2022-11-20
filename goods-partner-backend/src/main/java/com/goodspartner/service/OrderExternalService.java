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

    List<OrderExternal> findByDeliveryId(UUID deliveryId, OAuth2AuthenticationToken authentication);

    OrderExternal update(int id, OrderDto orderDto);

    void saveOrdersForDelivery(Delivery delivery);

    List<OrderExternal> saveValidOrdersAndEnrichKnownAddressesCache(List<OrderDto> orderDtos);

    List<OrderExternal> getSkippedOrders();

    List<OrderExternal> getCompletedOrders();

    List<OrderExternal> getScheduledOrders();

    List<OrderExternal> rescheduleSkippedOrders(RescheduleOrdersRequest rescheduleOrdersRequest);

    List<OrderExternal> removeExcludedOrders(RemoveOrdersRequest removeOrdersRequest);

}