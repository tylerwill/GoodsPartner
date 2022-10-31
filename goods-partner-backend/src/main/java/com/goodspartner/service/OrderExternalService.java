package com.goodspartner.service;

import com.goodspartner.action.OrderAction;
import com.goodspartner.dto.DeliveryDto;
import com.goodspartner.dto.OrderDto;
import com.goodspartner.entity.Delivery;
import com.goodspartner.entity.OrderExternal;
import com.goodspartner.entity.Car;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface OrderExternalService {

    void saveToOrderCache(UUID deliveryId, LocalDate date);

    List<OrderExternal> saveValidOrdersAndEnrichKnownAddressesCache(DeliveryDto deliveryDto);

    List<OrderDto> getOrdersFromCache(UUID deliveryId);

    void evictCache(UUID deliveryId);

    List<OrderDto> findOrdersByDeliveryAndCar(Delivery delivery, Car car);

    OrderDto updateDeliveryDate(int orderId, LocalDate deliveryDate, OrderAction action);

    List<OrderDto> getFilteredOrders(boolean excluded, boolean dropped);

}