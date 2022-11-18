package com.goodspartner.service;

import com.goodspartner.dto.OrderDto;
import com.goodspartner.web.controller.request.RemoveOrdersRequest;
import com.goodspartner.web.controller.request.RescheduleOrdersRequest;
import com.goodspartner.entity.Car;
import com.goodspartner.entity.Delivery;
import com.goodspartner.entity.OrderExternal;

import java.util.List;

public interface OrderExternalService {

    OrderDto update(int id, OrderDto orderDto);

    void saveOrdersForDelivery(Delivery delivery);

    List<OrderExternal> saveValidOrdersAndEnrichKnownAddressesCache(List<OrderDto> orderDtos);

    List<OrderDto> findOrdersByDeliveryAndCar(Delivery delivery, Car car);

    List<OrderDto> getSkippedOrders();

    List<OrderDto> getCompletedOrders();

    List<OrderDto> getScheduledOrders();

    List<OrderDto> rescheduleSkippedOrders(RescheduleOrdersRequest rescheduleOrdersRequest);

    List<OrderDto> removeExcludedOrders(RemoveOrdersRequest removeOrdersRequest);

}