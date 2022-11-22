package com.goodspartner.facade.impl;

import com.goodspartner.dto.DeliveryDto;
import com.goodspartner.entity.Delivery;
import com.goodspartner.entity.DeliveryFormationStatus;
import com.goodspartner.entity.DeliveryHistoryTemplate;
import com.goodspartner.entity.OrderExternal;
import com.goodspartner.entity.Route;
import com.goodspartner.entity.RouteStatus;
import com.goodspartner.exception.UnknownAddressException;
import com.goodspartner.facade.DeliveryFacade;
import com.goodspartner.service.DeliveryService;
import com.goodspartner.service.EventService;
import com.goodspartner.service.OrderExternalService;
import com.goodspartner.service.util.DeliveryCalculationHelper;
import com.goodspartner.web.action.DeliveryAction;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

import static com.goodspartner.entity.AddressStatus.UNKNOWN;

@Service
@AllArgsConstructor
public class DefaultDeliveryFacade implements DeliveryFacade {

    private final DeliveryService deliveryService;
    private final OrderExternalService orderService;
    private final EventService eventService;
    private final DeliveryCalculationHelper deliveryCalculationHelper;


    @Override
    public Delivery add(DeliveryDto deliveryDto) {
        Delivery delivery = deliveryService.add(deliveryDto);
        orderService.saveOrdersForDeliveryAsync(delivery); // Async
        eventService.publishDeliveryEvent(DeliveryHistoryTemplate.DELIVERY_CREATED, delivery.getId());
        return delivery;
    }

    @Override
    @Transactional
    public Delivery approve(UUID deliveryId, DeliveryAction action) {

        Delivery delivery = deliveryService.findById(deliveryId);

        action.perform(delivery);

        eventService.publishDeliveryEvent(DeliveryHistoryTemplate.DELIVERY_APPROVED, delivery.getId());

        List<Route> routes = delivery.getRoutes();
        routes.forEach(route -> route.setStatus(RouteStatus.APPROVED));
        routes.forEach(eventService::publishRouteStatusChangeAuto);

        return delivery;
    }

    @Transactional
    @Override
    public Delivery calculateDelivery(UUID deliveryId) {

        Delivery delivery = deliveryService.findById(deliveryId);

        List<OrderExternal> orders = delivery.getOrders();
        validateOrderAddresses(orders); // Do not create Delivery with UNKNOWN orders address
        resetOrders(orders); // If delivery already have order silently reset and exit for recalculation

        delivery.setFormationStatus(DeliveryFormationStatus.ROUTE_CALCULATION);

        deliveryCalculationHelper.calculate(deliveryId);  //Calculated in async method

        return delivery;
    }

    private void validateOrderAddresses(List<OrderExternal> orders) {
        orders.stream()
                .map(OrderExternal::getAddressExternal)
                .filter(addressExternal -> UNKNOWN.equals(addressExternal.getStatus()))
                .findFirst()
                .ifPresent(addressExternal -> {
                    throw new UnknownAddressException(addressExternal);
                });
    }

    private void resetOrders(List<OrderExternal> orders) {
        orders.forEach(orderExternal -> {
            orderExternal.setCarLoad(null);
            orderExternal.setRoutePoint(null);
            orderExternal.setDropped(false);
        });
    }

}
