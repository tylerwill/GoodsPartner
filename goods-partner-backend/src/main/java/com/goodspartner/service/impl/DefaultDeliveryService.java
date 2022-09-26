package com.goodspartner.service.impl;

import com.goodspartner.dto.DeliveryDto;
import com.goodspartner.entity.CarLoad;
import com.goodspartner.entity.Delivery;
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
import com.goodspartner.service.DeliveryService;
import com.goodspartner.service.RouteCalculationService;
import com.goodspartner.service.dto.RouteMode;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.ListUtils;
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

    @Override
    @Transactional(readOnly = true)
    public List<DeliveryDto> findAll() {
        return deliveryMapper.deliveriesToDeliveryDtos(deliveryRepository.findAll());
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

        return deliveryMapper.toDeliveryDtoResult(new DeliveryDto(), addedDelivery);
    }

    @Override
    @Transactional
    public DeliveryDto update(UUID deliveryId, DeliveryDto deliveryDto) {
        checkStatus(deliveryDto);

        Delivery deliveryToUpdate = deliveryRepository.findById(deliveryId)
                .map(delivery -> deliveryMapper.update(delivery, deliveryDto))
                .orElseThrow(() -> new DeliveryNotFoundException(deliveryId));

        Delivery updatedDelivery = deliveryRepository.save(deliveryToUpdate);
        return deliveryMapper.toDeliveryDtoResult(new DeliveryDto(), updatedDelivery);
    }

    @Override
    @Transactional
    public DeliveryDto calculateDelivery(UUID deliveryId) {
        Delivery delivery = deliveryRepository.findById(deliveryId)
                .orElseThrow(() -> new DeliveryNotFoundException(deliveryId));

        validateDelivery(delivery);

        List<OrderExternal> orders = delivery.getOrders();

        // Frozen Orders
        List<Route> coolerRoutes = routeCalculationService.calculateRoutes(orders, RouteMode.COOLER);
        List<CarLoad> coolerCarLoad = carLoadService.buildCarLoad(coolerRoutes, orders);

        // Other orders
        List<Route> regularRoutes = routeCalculationService.calculateRoutes(orders, RouteMode.REGULAR);
        List<CarLoad> regularCarLoads = carLoadService.buildCarLoad(regularRoutes, orders);

        // Update Delivery
        delivery.setRoutes(ListUtils.union(coolerRoutes, regularRoutes));
        delivery.setCarLoads(ListUtils.union(coolerCarLoad, regularCarLoads));

        return deliveryMapper.deliveryToDeliveryDto(deliveryRepository.save(delivery));
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
