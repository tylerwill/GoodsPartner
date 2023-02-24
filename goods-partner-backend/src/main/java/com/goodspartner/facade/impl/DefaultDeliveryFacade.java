package com.goodspartner.facade.impl;

import com.goodspartner.dto.DeliveryDto;
import com.goodspartner.entity.Delivery;
import com.goodspartner.entity.DeliveryFormationStatus;
import com.goodspartner.entity.DeliveryHistoryTemplate;
import com.goodspartner.entity.Route;
import com.goodspartner.entity.RouteStatus;
import com.goodspartner.exception.delivery.IllegalDeliveryStateForRecalculation;
import com.goodspartner.facade.DeliveryFacade;
import com.goodspartner.facade.OrderFacade;
import com.goodspartner.service.DeliveryService;
import com.goodspartner.service.EventService;
import com.goodspartner.service.util.DeliveryCalculationHelper;
import com.goodspartner.web.action.DeliveryAction;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

import static com.goodspartner.entity.DeliveryStatus.DRAFT;

@Service
@RequiredArgsConstructor
public class DefaultDeliveryFacade implements DeliveryFacade {

    private final DeliveryService deliveryService;
    private final OrderFacade orderFacade;
    private final EventService eventService;
    private final DeliveryCalculationHelper deliveryCalculationHelper;

    @Override
    public Delivery add(DeliveryDto deliveryDto) {
        Delivery delivery = deliveryService.add(deliveryDto);
        orderFacade.processOrdersForDeliveryAsync(delivery); // Async
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

        orderFacade.validateOrdersForDeliveryCalculation(deliveryId); // Do not create Delivery with UNKNOWN orders address

        Delivery delivery = deliveryService.findById(deliveryId);
        if (!DRAFT.equals(delivery.getStatus())) { //  Delivery recalculation only for Draft
            throw new IllegalDeliveryStateForRecalculation();
        }

        delivery.setFormationStatus(DeliveryFormationStatus.ROUTE_CALCULATION);

        deliveryCalculationHelper.calculate(deliveryId);  // Calculated in async method

        return delivery;
    }

}
