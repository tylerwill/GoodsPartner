package com.goodspartner.service.impl;

import com.goodspartner.action.OrderAction;
import com.goodspartner.cache.OrderCache;
import com.goodspartner.dto.DeliveryDto;
import com.goodspartner.dto.OrderDto;
import com.goodspartner.entity.AddressExternal;
import com.goodspartner.entity.Car;
import com.goodspartner.entity.Delivery;
import com.goodspartner.entity.DeliveryFormationStatus;
import com.goodspartner.entity.DeliveryHistoryTemplate;
import com.goodspartner.entity.DeliveryStatus;
import com.goodspartner.entity.OrderExternal;
import com.goodspartner.exception.DeliveryNotFoundException;
import com.goodspartner.exception.OrderNotFoundException;
import com.goodspartner.exception.UnknownAddressException;
import com.goodspartner.mapper.OrderExternalMapper;
import com.goodspartner.repository.AddressExternalRepository;
import com.goodspartner.repository.DeliveryRepository;
import com.goodspartner.repository.OrderExternalRepository;
import com.goodspartner.service.EventService;
import com.goodspartner.service.GeocodeService;
import com.goodspartner.service.IntegrationService;
import com.goodspartner.service.OrderExternalService;
import com.goodspartner.service.util.ExternalOrderPostProcessor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.goodspartner.dto.MapPoint.AddressStatus.UNKNOWN;

@RequiredArgsConstructor
@Slf4j
@Service
public class DefaultOrderExternalService implements OrderExternalService {

    private final OrderExternalMapper orderExternalMapper;
    private final OrderExternalRepository orderExternalRepository;
    private final DeliveryRepository deliveryRepository;
    private final AddressExternalRepository addressExternalRepository;
    private final IntegrationService integrationService; // GrangeDolceIntegration
    private final GeocodeService geocodeService;
    private final ExternalOrderPostProcessor orderCommentProcessor;
    private final OrderCache orderCache;
    private final EventService eventService;

    @Override
    @Async("goodsPartnerThreadPoolTaskExecutor")
    public void saveToOrderCache(UUID deliveryId, LocalDate date) {
        log.info("Fetching orders from 1C");

        List<OrderDto> orders = integrationService.findAllByShippingDate(date);
        if (orders.isEmpty()) {
            log.warn("No orders where found in 1C for date: {}", date);
            return;
        }

        if (!orders.isEmpty()) {
            eventService.publishOrdersStatus(DeliveryHistoryTemplate.ORDERS_LOADING, deliveryId);

            orderCommentProcessor.processOrderComments(orders);
            geocodeService.enrichValidAddress(orders);
            orderCache.saveOrders(deliveryId, orders);

            eventService.publishOrdersStatus(DeliveryHistoryTemplate.ORDERS_LOADED, deliveryId);
            log.info("Saved to cache orders for delivery {} on {} date", deliveryId, date);
        }
    }

    @Override
    @Transactional
    public List<OrderExternal> saveValidOrdersAndEnrichKnownAddressesCache(DeliveryDto deliveryDto) {

        UUID deliveryId = deliveryDto.getId();
        List<OrderDto> orderDtos = deliveryDto.getOrders();

        validateOrderAddresses(orderDtos);

        Delivery delivery = deliveryRepository.findById(deliveryId)
                .orElseThrow(() -> new DeliveryNotFoundException(deliveryId));

        List<OrderExternal> externalOrders = orderExternalMapper.mapOrderDtosToOrdersExternal(orderDtos);
        externalOrders.forEach(externalOrder -> externalOrder.setDelivery(delivery));

        delivery.setFormationStatus(DeliveryFormationStatus.ORDERS_LOADED);
        deliveryRepository.save(delivery);

        Set<AddressExternal> addresses = externalOrders
                .stream()
                .map(OrderExternal::getAddressExternal)
                .collect(Collectors.toSet());
        List<AddressExternal> addressExternals = addressExternalRepository.saveAll(addresses);

        Map<AddressExternal.OrderAddressId, AddressExternal> addressesMap = addressExternals
                .stream()
                .collect(Collectors.toMap(
                        AddressExternal::getOrderAddressId,
                        addressExternal -> addressExternal,
                        (existentAddress, newAddress) -> {
                            log.info("Duplicate address found: {}", newAddress);
                            return existentAddress;
                        }));

        externalOrders.forEach(externalOrder -> {
            AddressExternal.OrderAddressId orderAddressId = externalOrder.getAddressExternal().getOrderAddressId();
            AddressExternal persistedAddressExternal = addressesMap.get(orderAddressId);
            externalOrder.setAddressExternal(persistedAddressExternal);
        });

        orderExternalRepository.saveAll(externalOrders);

        return externalOrders;
    }

    @Override
    public List<OrderDto> getOrdersFromCache(UUID deliveryId) {
        Optional<List<OrderDto>> orders = orderCache.getOrders(deliveryId);

        if (orders.isPresent()) {
            return orders.get();
        }

        log.info("There is no orders in cache for Delivery - {}", deliveryId);

        return Collections.emptyList();
    }

    @Override
    public void evictCache(UUID deliveryId) {
        List<OrderDto> orderDtos = orderCache.removeOrders(deliveryId);

        if (orderDtos == null) {
            log.info("There are no orders in cache for Delivery: {}", deliveryId);
        } else {
            log.info("Orders removed from cache for Delivery: {}", deliveryId);
        }
    }

    @Override
    public List<OrderDto> findOrdersByDeliveryAndCar(Delivery delivery, Car car) {
        return orderExternalRepository.findAllByDeliveryAndCar(delivery, car)
                .stream()
                .map(orderExternalMapper::mapOrderExternalToOrderDto)
                .toList();

    }

    @Override
    public List<OrderDto> getFilteredOrders(boolean excluded, boolean dropped) {
        return orderExternalRepository.findAllByExcludedAndDropped(excluded, dropped)
                .stream()
                .map(orderExternalMapper::mapOrderExternalToOrderDto)
                .toList();
    }

    @Transactional
    @Override
    public OrderDto updateDeliveryDate(int orderId, LocalDate deliveryDate, OrderAction action) {
        OrderExternal orderExternal = orderExternalRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException(orderId));

        orderExternal.setDeliveryDate(deliveryDate);
        action.perform(orderExternal);

        Optional<Delivery> delivery = deliveryRepository.findByStatusAndDeliveryDate(DeliveryStatus.DRAFT, deliveryDate);
        delivery.ifPresent(orderExternal::setDelivery);

        return orderExternalMapper.mapOrderExternalToOrderDto(orderExternalRepository.save(orderExternal));
    }

    private void validateOrderAddresses(List<OrderDto> orderDtos) {
        orderDtos.stream()
                .map(OrderDto::getMapPoint)
                .filter(mapPoint -> UNKNOWN.equals(mapPoint.getStatus()))
                .findFirst()
                .ifPresent(mapPoint -> {
                    throw new UnknownAddressException("address " + mapPoint.getAddress() + " is in Unknown status");
                });
    }
}