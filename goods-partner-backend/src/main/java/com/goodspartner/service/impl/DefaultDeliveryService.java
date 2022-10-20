package com.goodspartner.service.impl;

import com.goodspartner.action.DeliveryAction;
import com.goodspartner.web.controller.response.DeliveryActionResponse;
import com.goodspartner.dto.DeliveryDto;
import com.goodspartner.dto.DeliveryShortDto;
import com.goodspartner.entity.CarLoad;
import com.goodspartner.entity.Delivery;
import com.goodspartner.entity.DeliveryHistoryTemplate;
import com.goodspartner.entity.DeliveryStatus;
import com.goodspartner.entity.OrderExternal;
import com.goodspartner.entity.Route;
import com.goodspartner.entity.RouteStatus;
import com.goodspartner.exceptions.DeliveryModifyException;
import com.goodspartner.exceptions.DeliveryNotFoundException;
import com.goodspartner.exceptions.IllegalDeliveryStatusForOperation;
import com.goodspartner.exceptions.NoOrdersFoundForDelivery;
import com.goodspartner.mapper.DeliveryMapper;
import com.goodspartner.repository.DeliveryRepository;
import com.goodspartner.service.CarLoadService;
import com.goodspartner.service.DeliveryHistoryService;
import com.goodspartner.service.DeliveryService;
import com.goodspartner.service.RouteCalculationService;
import com.goodspartner.service.dto.RouteMode;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.ListUtils;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.goodspartner.entity.DeliveryStatus.DRAFT;

@Slf4j
@AllArgsConstructor
@Service
public class DefaultDeliveryService implements DeliveryService {

    private final DeliveryMapper deliveryMapper;
    private final DeliveryRepository deliveryRepository;
    private final RouteCalculationService routeCalculationService;
    private final CarLoadService carLoadService;
    private final DeliveryHistoryService deliveryHistoryService;

    @Override
    @Transactional(readOnly = true)
    public List<DeliveryShortDto> findAll() {
        Sort sortByDeliveryDate = Sort.by(Sort.Direction.DESC, "deliveryDate");
        return deliveryRepository.findAll(sortByDeliveryDate)
                .stream()
                .map(deliveryMapper::deliveryToDeliveryShortDto)
                .toList();
    }

    @Override
    public DeliveryDto delete(UUID deliveryId) {
        Delivery deliveryToDelete = deliveryRepository.findById(deliveryId)
                .orElseThrow(() -> new DeliveryNotFoundException(deliveryId));

        if (DRAFT != deliveryToDelete.getStatus()) {
            throw new IllegalDeliveryStatusForOperation(deliveryToDelete, "delete");
        }

        deliveryRepository.deleteById(deliveryId);

        return deliveryMapper.toDeliveryDtoResult(new DeliveryDto(), deliveryToDelete);
    }

    @Override
    @Transactional(readOnly = true)
    public DeliveryDto findById(UUID deliveryId) {
        return deliveryRepository.findById(deliveryId)
                .map(deliveryMapper::deliveryToDeliveryDto)
                .orElseThrow(() -> new DeliveryNotFoundException(deliveryId));
    }

    @Override
    @Transactional
    public DeliveryDto add(DeliveryDto deliveryDto) {
        Optional<Delivery> optionalDelivery = deliveryRepository.findByDeliveryDate(deliveryDto.getDeliveryDate());
        if (optionalDelivery.isPresent()) {
            throw new IllegalArgumentException("There is already delivery on date: " + deliveryDto.getDeliveryDate());
        }

        Delivery addedDelivery = deliveryRepository.save(deliveryMapper.deliveryDtoToDelivery(deliveryDto));

        DeliveryDto addedDeliveryDto = deliveryMapper.toDeliveryDtoResult(new DeliveryDto(), addedDelivery);

        deliveryHistoryService.publishDeliveryEvent(DeliveryHistoryTemplate.DELIVERY_CREATED, addedDeliveryDto.getId());

        return addedDeliveryDto;
    }

    @Override
    @Transactional
    public DeliveryDto update(UUID deliveryId, DeliveryDto deliveryDto) {
        checkStatus(deliveryDto);

        Delivery deliveryToUpdate = deliveryRepository.findById(deliveryId)
                .map(delivery -> deliveryMapper.update(delivery, deliveryDto))
                .orElseThrow(() -> new DeliveryNotFoundException(deliveryId));

        Delivery updatedDelivery = deliveryRepository.save(deliveryToUpdate);

        deliveryHistoryService.publishDeliveryEvent(DeliveryHistoryTemplate.DELIVERY_UPDATED, updatedDelivery.getId());

        return deliveryMapper.toDeliveryDtoResult(new DeliveryDto(), updatedDelivery);
    }

    @Override
    @Transactional
    public DeliveryDto calculateDelivery(UUID deliveryId) {
        Delivery delivery = deliveryRepository.findById(deliveryId)
                .orElseThrow(() -> new DeliveryNotFoundException(deliveryId));

        validateDelivery(delivery);

        // Cleanup in case of recalculation
        List<OrderExternal> orders = delivery.getOrders();
        orders.forEach(orderExternal -> orderExternal.setCarLoad(null));

        List<OrderExternal> includedOrders = orders.stream()
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

        deliveryHistoryService.publishDeliveryEvent(DeliveryHistoryTemplate.DELIVERY_CALCULATED, deliveryId);

        return deliveryMapper.deliveryToDeliveryDto(deliveryRepository.save(delivery));
    }

    @Override
    @Transactional
    public DeliveryActionResponse approve(UUID deliveryId, DeliveryAction action) {

        Delivery delivery = deliveryRepository.findById(deliveryId)
                .orElseThrow(() -> new DeliveryNotFoundException(deliveryId));

        action.perform(delivery);

        deliveryHistoryService.publishDeliveryEvent(DeliveryHistoryTemplate.DELIVERY_APPROVED, delivery.getId());

        List<Route> routes = delivery.getRoutes();
        routes.forEach(route -> route.setStatus(RouteStatus.APPROVED));
        routes.forEach(deliveryHistoryService::publishRouteStatusChangeAuto);

        deliveryRepository.save(delivery);

        return mapDeliveryActionResponse(delivery, routes);
    }

    private DeliveryActionResponse mapDeliveryActionResponse(Delivery delivery, List<Route> routes) {
        DeliveryActionResponse deliveryActionResponse = new DeliveryActionResponse();
        deliveryActionResponse.setDeliveryId(delivery.getId());
        deliveryActionResponse.setDeliveryStatus(delivery.getStatus());

        List<DeliveryActionResponse.RoutesStatus> routeStatuses = routes.stream()
                .map(route -> new DeliveryActionResponse.RoutesStatus(route.getId(), route.getStatus()))
                .toList();
        deliveryActionResponse.setRoutesStatus(routeStatuses);

        return deliveryActionResponse;
    }


    private void validateDelivery(Delivery delivery) {
        if (delivery.getStatus() != DRAFT) {
            throw new IllegalDeliveryStatusForOperation(delivery, "calculate");
        }

        if (delivery.getOrders().isEmpty()) {
            throw new NoOrdersFoundForDelivery(delivery.getId());
        }
    }

    private void checkStatus(DeliveryDto deliveryDto) {
        if (deliveryDto.getStatus().equals(DeliveryStatus.COMPLETED)) {
            if (!isAllRoutesCompleted(deliveryDto)) {
                throw new DeliveryModifyException("Not possible to close the delivery due to not all routes have been completed");
            }
        }
    }

    private boolean isAllRoutesCompleted(DeliveryDto deliveryDto) {
        return deliveryDto.getRoutes().stream()
                .filter(route -> !route.getStatus().equals(RouteStatus.COMPLETED))
                .findFirst()
                .isEmpty();
    }
}
