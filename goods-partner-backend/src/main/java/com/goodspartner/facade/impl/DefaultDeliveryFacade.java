package com.goodspartner.facade.impl;

import com.goodspartner.dto.DeliveryDto;
import com.goodspartner.entity.Delivery;
import com.goodspartner.entity.DeliveryFormationStatus;
import com.goodspartner.entity.Route;
import com.goodspartner.entity.RouteStatus;
import com.goodspartner.event.ActionType;
import com.goodspartner.facade.DeliveryFacade;
import com.goodspartner.facade.OrderFacade;
import com.goodspartner.service.DeliveryService;
import com.goodspartner.service.EventService;
import com.goodspartner.service.OrderExternalService;
import com.goodspartner.service.util.DeliveryCalculationHelper;
import com.goodspartner.web.action.DeliveryAction;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

import static com.goodspartner.entity.DeliveryStatus.DRAFT;
import static com.goodspartner.event.ActionType.ROUTE_UPDATED;
import static com.goodspartner.event.EventMessageTemplate.DELIVERY_APPROVED;
import static com.goodspartner.event.EventMessageTemplate.DELIVERY_CREATED;
import static com.goodspartner.event.EventMessageTemplate.DELIVERY_SYNCHRONIZATION;
import static com.goodspartner.event.EventMessageTemplate.ROUTE_STATUS_AUTO;
import static com.goodspartner.event.EventType.INFO;

@Service
@RequiredArgsConstructor
public class DefaultDeliveryFacade implements DeliveryFacade {

    private final OrderFacade orderFacade;

    private final DeliveryService deliveryService;
    private final EventService eventService;
    private final OrderExternalService orderExternalService;

    private final DeliveryCalculationHelper deliveryCalculationHelper;

    @Override
    public Delivery add(DeliveryDto deliveryDto) {
        Delivery delivery = deliveryService.add(deliveryDto);
        orderFacade.processOrdersForDeliveryAsync(delivery); // Async
        eventService.publishForLogist(DELIVERY_CREATED.withAudit(), INFO, ActionType.DELIVERY_CREATED, delivery.getId());
        return delivery;
    }

    @Transactional
    @Override
    public Delivery approve(UUID deliveryId, DeliveryAction action) {

        Delivery delivery = deliveryService.findById(deliveryId);

        action.perform(delivery);

        eventService.publishForLogist(DELIVERY_APPROVED.withAudit(), INFO, ActionType.DELIVERY_UPDATED, deliveryId);

        List<Route> routes = delivery.getRoutes();
        routes.forEach(route -> route.setStatus(RouteStatus.APPROVED));
        routes.forEach(route -> eventService.publishForDriverAndLogist(ROUTE_STATUS_AUTO.withRouteValues(route), INFO, ROUTE_UPDATED, route));

        return delivery;
    }

    @Transactional
    @Override
    public Delivery calculateDelivery(UUID deliveryId) {

        orderFacade.validateOrdersForDeliveryCalculation(deliveryId); // Do not create Delivery with UNKNOWN orders address

        Delivery delivery = deliveryService.findById(deliveryId);
        delivery.setStatus(DRAFT); // Force draft status if recalculation for approved
        delivery.setFormationStatus(DeliveryFormationStatus.ROUTE_CALCULATION);

        deliveryCalculationHelper.calculate(delivery);  // Calculated in async method

        return delivery;
    }

    @Override
    public void resyncOrders(UUID deliveryId) {

        Delivery delivery = deliveryService.cleanupDeliveryForOrdersSync(deliveryId);

        orderExternalService.cleanupOrders(deliveryId);

        eventService.publishForLogist(DELIVERY_SYNCHRONIZATION.withAudit(), INFO, ActionType.ORDER_UPDATED, deliveryId);

        orderFacade.processOrdersForDeliveryAsync(delivery); // Async

    }
}
