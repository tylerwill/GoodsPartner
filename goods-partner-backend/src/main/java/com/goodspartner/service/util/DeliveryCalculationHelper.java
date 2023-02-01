package com.goodspartner.service.util;

import com.goodspartner.entity.CarLoad;
import com.goodspartner.entity.Delivery;
import com.goodspartner.entity.DeliveryFormationStatus;
import com.goodspartner.entity.DeliveryHistoryTemplate;
import com.goodspartner.entity.OrderExternal;
import com.goodspartner.entity.Route;
import com.goodspartner.event.Action;
import com.goodspartner.event.ActionType;
import com.goodspartner.event.EventType;
import com.goodspartner.event.LiveEvent;
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
    public void calculate(UUID deliveryId) {
        log.info("Started calculation for delivery with id: {}", deliveryId);

        try {
            List<OrderExternal> includedOrders = orderExternalRepository.findOrdersForCalculation(deliveryId);
            resetOrders(includedOrders); // If delivery already have order silently reset and exit for recalculation

            // Routes
            List<Route> coolerRoutes = routeCalculationService.calculateRoutes(includedOrders, RouteMode.COOLER);

            List<Route> regularRoutes = routeCalculationService.calculateRoutes(includedOrders, RouteMode.REGULAR);

            // CarLoad
            List<CarLoad> coolerCarLoad = carLoadService.buildCarLoad(coolerRoutes, includedOrders);
            List<CarLoad> regularCarLoads = carLoadService.buildCarLoad(regularRoutes, includedOrders);

            List<Route> routes = ListUtils.union(coolerRoutes, regularRoutes);
            List<CarLoad> carLoads = ListUtils.union(coolerCarLoad, regularCarLoads);

            // Update Delivery
            txWrapper.runNewTransaction(() -> {
                log.info("Saving calculation result ot delivery");
                Delivery delivery = deliveryRepository.findById(deliveryId)
                        .orElseThrow(() -> new DeliveryNotFoundException(deliveryId));
                delivery.setRoutes(routes);
                delivery.setRouteCount(routes.size());
                delivery.setCarLoads(carLoads);
                delivery.setFormationStatus(DeliveryFormationStatus.CALCULATION_COMPLETED);
                return deliveryRepository.save(delivery);
            });

            log.info("Sending notification for calculation status");
            eventService.publishDeliveryEvent(DeliveryHistoryTemplate.DELIVERY_CALCULATED, deliveryId);
            eventService.publishDroppedOrdersEvent(deliveryId, includedOrders);
            eventService.publishRoutesUpdated(deliveryId);
            // TODO routeUpdate required on FE as well
            log.info("Finished calculation for delivery with id: {}", deliveryId);
        } catch (Exception exception) {
            eventService.publishEvent(new LiveEvent("Помилка розрахування доставки", EventType.ERROR,
                    new Action(ActionType.INFO, deliveryId)));
            throw new RuntimeException(exception);
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
