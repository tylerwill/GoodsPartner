package com.goodspartner.service.util;

import com.goodspartner.entity.CarLoad;
import com.goodspartner.entity.Delivery;
import com.goodspartner.entity.DeliveryFormationStatus;
import com.goodspartner.entity.OrderExternal;
import com.goodspartner.entity.Route;
import com.goodspartner.event.ActionType;
import com.goodspartner.event.EventType;
import com.goodspartner.exception.DeliveryNotFoundException;
import com.goodspartner.repository.DeliveryRepository;
import com.goodspartner.repository.OrderExternalRepository;
import com.goodspartner.service.CarLoadService;
import com.goodspartner.service.EventService;
import com.goodspartner.service.RouteCalculationService;
import com.goodspartner.service.dto.RouteMode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.ListUtils;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

import static com.goodspartner.event.EventMessageTemplate.DELIVERY_CALCULATED;
import static com.goodspartner.event.EventMessageTemplate.DELIVERY_CALCULATION_FAILED;
import static com.goodspartner.event.EventMessageTemplate.DROPPED_ORDERS;
import static com.goodspartner.event.EventMessageTemplate.EventPlaceholder.DROPPED_ORDERS_AMOUNT;
import static com.goodspartner.event.EventMessageTemplate.ORDERS_UPDATED;
import static com.goodspartner.event.EventMessageTemplate.ROUTES_UPDATED;
import static com.goodspartner.event.EventType.ERROR;
import static com.goodspartner.event.EventType.SUCCESS;

@Component
@RequiredArgsConstructor
@Slf4j
public class DeliveryCalculationHelper {

    private final RouteCalculationService routeCalculationService;
    private final CarLoadService carLoadService;
    private final EventService eventService;
    private final TxWrapper txWrapper;

    private final OrderExternalRepository orderExternalRepository;
    private final DeliveryRepository deliveryRepository;

    @Async("goodsPartnerThreadPoolTaskExecutor")
    public void calculate(Delivery delivery) {
        UUID deliveryId = delivery.getId();
        log.info("Started calculation for delivery with id: {} by date: {}", deliveryId, delivery.getDeliveryDate());

        try {
            List<OrderExternal> includedOrders = orderExternalRepository.findOrdersForCalculation(deliveryId);
            resetOrders(includedOrders); // If delivery already have order silently resets

            // Routes
            List<Route> coolerRoutes = routeCalculationService.calculateRoutes(includedOrders, RouteMode.COOLER);
            List<Route> regularRoutes = routeCalculationService.calculateRoutes(includedOrders, RouteMode.REGULAR);

            // CarLoad
            List<CarLoad> coolerCarLoad = carLoadService.buildCarLoad(coolerRoutes, includedOrders);
            List<CarLoad> regularCarLoads = carLoadService.buildCarLoad(regularRoutes, includedOrders);

            List<Route> routes = ListUtils.union(coolerRoutes, regularRoutes);
            List<CarLoad> carLoads = ListUtils.union(coolerCarLoad, regularCarLoads);

            // Update Delivery require separate transaction to avoid lazyinitexcp
            txWrapper.runNewTransaction(() -> {
                log.info("Saving calculation result ot delivery, id: {}, date: {}", delivery.getId(), delivery.getDeliveryDate());
                Delivery savedDelivery = deliveryRepository.findById(deliveryId)
                        .orElseThrow(() -> new DeliveryNotFoundException(deliveryId));
                savedDelivery.setRoutes(routes);
                savedDelivery.setRouteCount(routes.size());
                savedDelivery.setCarLoads(carLoads);
                savedDelivery.setFormationStatus(DeliveryFormationStatus.CALCULATION_COMPLETED);
                return deliveryRepository.save(savedDelivery);
            });

            log.info("Sending notification for calculation status for delivery, id: {}, date: {}", delivery.getId(), delivery.getDeliveryDate());
            publishEvents(deliveryId, includedOrders);
            log.info("Finished calculation for delivery with id: {}", deliveryId);
        } catch (Exception exception) {
            // Update delivery status
            delivery.setFormationStatus(DeliveryFormationStatus.ROUTE_CALCULATION_FAILED);
            deliveryRepository.save(delivery);
            // Notify failed
            eventService.publishForLogist(DELIVERY_CALCULATION_FAILED.getTemplate(), ERROR, ActionType.DELIVERY_UPDATED, deliveryId);
            log.error("Exception while async delivery calculation: {}", delivery, exception);
        }
    }

    private void publishEvents(UUID deliveryId, List<OrderExternal> includedOrders) {
        eventService.publishForLogist(DELIVERY_CALCULATED.withAudit(), SUCCESS, ActionType.DELIVERY_UPDATED, deliveryId);
        eventService.publishForLogist(ROUTES_UPDATED.getTemplate(), EventType.INFO, ActionType.ROUTE_UPDATED, deliveryId);

        List<OrderExternal> droppedOrders = includedOrders.stream().filter(OrderExternal::isDropped).toList();
        if (droppedOrders.isEmpty()) {
            eventService.publishForLogist(ORDERS_UPDATED.getTemplate(), EventType.INFO, ActionType.ORDER_UPDATED, deliveryId);
        } else {
            String eventMessage = DROPPED_ORDERS.withValues(DROPPED_ORDERS_AMOUNT, String.valueOf(droppedOrders.size()));
            eventService.publishForLogist(eventMessage, EventType.WARNING, ActionType.ORDER_UPDATED, deliveryId);
        }
    }

    private void resetOrders(List<OrderExternal> orders) {
        orders.forEach(orderExternal -> {
            orderExternal.setCarLoad(null);
            orderExternal.setRoutePoint(null);
            orderExternal.setDropped(false);
        });
    }
}
