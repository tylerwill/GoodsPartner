package com.goodspartner.service.util;

import com.goodspartner.entity.CarLoad;
import com.goodspartner.entity.Delivery;
import com.goodspartner.entity.DeliveryFormationStatus;
import com.goodspartner.entity.DeliveryHistoryTemplate;
import com.goodspartner.entity.OrderExternal;
import com.goodspartner.entity.Route;
import com.goodspartner.exception.DeliveryNotFoundException;
import com.goodspartner.repository.DeliveryRepository;
import com.goodspartner.service.CarLoadService;
import com.goodspartner.service.DeliveryHistoryService;
import com.goodspartner.service.OrderExternalService;
import com.goodspartner.service.RouteCalculationService;
import com.goodspartner.service.dto.RouteMode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.ListUtils;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class DeliveryCalculationHelper {

    private final RouteCalculationService routeCalculationService;
    private final CarLoadService carLoadService;
    private final DeliveryHistoryService deliveryHistoryService;
    private final OrderExternalService orderExternalService;
    private final DeliveryRepository deliveryRepository;

    @Async
    @Transactional
    public void calculate(UUID deliveryId) {

        Delivery delivery = deliveryRepository.findById(deliveryId)
                .orElseThrow(() -> new DeliveryNotFoundException(deliveryId));

        List<OrderExternal> orderExternals = delivery.getOrders();

        List<OrderExternal> includedOrders = orderExternals.stream()
                .filter(orderExternal -> !orderExternal.isExcluded()).toList();
        // Routes
        List<Route> coolerRoutes = routeCalculationService.calculateRoutes(includedOrders, RouteMode.COOLER);
        List<Route> regularRoutes = routeCalculationService.calculateRoutes(includedOrders, RouteMode.REGULAR);

        // CarLoad
        List<CarLoad> coolerCarLoad = carLoadService.buildCarLoad(coolerRoutes, includedOrders);
        List<CarLoad> regularCarLoads = carLoadService.buildCarLoad(regularRoutes, includedOrders);

        // Update Delivery
        delivery.setRoutes(ListUtils.union(coolerRoutes, regularRoutes));
        delivery.setCarLoads(ListUtils.union(coolerCarLoad, regularCarLoads));

        delivery.setFormationStatus(DeliveryFormationStatus.COMPLETED);
        deliveryHistoryService.publishDeliveryEvent(DeliveryHistoryTemplate.DELIVERY_CALCULATED, deliveryId);

        deliveryRepository.save(delivery);

        log.info("Delivery: {} has been calculated", deliveryId);

        orderExternalService.evictCache(deliveryId);

        log.info("Cache has been evicted for Delivery: {}", deliveryId);
    }
}
