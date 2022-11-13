package com.goodspartner.service;

import com.goodspartner.action.OrderAction;
import com.goodspartner.dto.OrderDto;
import com.goodspartner.dto.RescheduleOrdersDto;
import com.goodspartner.entity.Car;
import com.goodspartner.entity.Delivery;
import com.goodspartner.entity.OrderExternal;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface OrderExternalService {

    void saveToOrderCache(UUID deliveryId, LocalDate date);

    List<OrderExternal> saveValidOrdersAndEnrichKnownAddressesCache(List<OrderDto> orderDtos);

    List<OrderDto> getOrdersFromCache(UUID deliveryId);

    void evictCache(UUID deliveryId);

    List<OrderDto> findOrdersByDeliveryAndCar(Delivery delivery, Car car);

    List<OrderDto> rescheduleOrders(RescheduleOrdersDto rescheduleOrdersDto, OrderAction orderAction);

    List<OrderDto> getSkippedOrders();

    List<OrderDto> getCompletedOrders();

}