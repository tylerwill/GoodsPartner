package com.goodspartner.service.impl;

import com.goodspartner.dto.OrderDto;
import com.goodspartner.web.controller.request.RemoveOrdersRequest;
import com.goodspartner.web.controller.request.RescheduleOrdersRequest;
import com.goodspartner.entity.AddressExternal;
import com.goodspartner.entity.Car;
import com.goodspartner.entity.Delivery;
import com.goodspartner.entity.DeliveryFormationStatus;
import com.goodspartner.entity.DeliveryHistoryTemplate;
import com.goodspartner.entity.OrderExternal;
import com.goodspartner.event.EventType;
import com.goodspartner.event.LiveEvent;
import com.goodspartner.exception.OrderNotFoundException;
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
import org.apache.commons.collections4.ListUtils;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Slf4j
@Service
public class DefaultOrderExternalService implements OrderExternalService {

    private final OrderExternalMapper orderExternalMapper;

    private final OrderExternalRepository orderExternalRepository;
    private final AddressExternalRepository addressExternalRepository;
    private final DeliveryRepository deliveryRepository;

    private final IntegrationService integrationService; // GrangeDolceIntegration
    private final GeocodeService geocodeService;
    private final ExternalOrderPostProcessor orderCommentProcessor;
    private final EventService eventService;

    @Override
    public List<OrderDto> findByDeliveryId(UUID deliveryId) {
        return orderExternalMapper.mapToDtos(orderExternalRepository.findAllByDelivery(deliveryId));
    }

    @Transactional
    @Override
    public OrderDto update(int id, OrderDto orderDto) {
        log.info("Updating order with id: {}", id);
        OrderExternal order = orderExternalRepository.findById(id)
                .orElseThrow(() -> new OrderNotFoundException(id));
        orderExternalMapper.update(order, orderDto);
        return orderExternalMapper.mapToDto(orderExternalRepository.save(order));
    }

    @Override
    @Async("goodsPartnerThreadPoolTaskExecutor")
    @Transactional
    public void saveOrdersForDelivery(Delivery delivery) {
        UUID deliveryId = delivery.getId();
        LocalDate deliveryDate = delivery.getDeliveryDate();

        log.info("Fetching orders from 1C");
        try {
            eventService.publishOrdersStatus(DeliveryHistoryTemplate.ORDERS_LOADING, deliveryId);
            List<OrderDto> orderDtos = integrationService.findAllByShippingDate(deliveryDate);
            if (orderDtos.isEmpty()) {
                log.warn("No orders where found in 1C for deliveryDate: {}", deliveryDate);
                return;
            }

            orderCommentProcessor.processOrderComments(orderDtos);

            geocodeService.enrichValidAddress(orderDtos);

            List<OrderExternal> orderEntities = saveValidOrdersAndEnrichKnownAddressesCache(orderDtos);
            List<OrderExternal> scheduledOrders = orderExternalRepository.findByRescheduleDate(deliveryDate);
            List<OrderExternal> allOrdersByDate = ListUtils.union(orderEntities, scheduledOrders);

            delivery.setOrders(allOrdersByDate);
            delivery.setFormationStatus(DeliveryFormationStatus.ORDERS_LOADED);
            deliveryRepository.save(delivery);

            eventService.publishOrdersStatus(DeliveryHistoryTemplate.ORDERS_LOADED, deliveryId);
            log.info("Saved orders for delivery {} on {} deliveryDate", deliveryId, deliveryDate);
        } catch (Exception exception) {
            eventService.publishEvent(new LiveEvent("Помилка під час вивантаження замовлень з 1С", EventType.ERROR));
            throw new RuntimeException(exception);
        }
    }

    @Override
    @Transactional
    public List<OrderExternal> saveValidOrdersAndEnrichKnownAddressesCache(List<OrderDto> orderDtos) {

        List<OrderExternal> externalOrders = orderDtos.stream()
                .map(orderExternalMapper::mapToEntity)
                .toList();

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

        return orderExternalRepository.saveAll(externalOrders);
    }

    @Override
    public List<OrderDto> findOrdersByDeliveryAndCar(Delivery delivery, Car car) {
        return orderExternalRepository.findAllByDeliveryAndCar(delivery, car)
                .stream()
                .map(orderExternalMapper::mapToDto)
                .toList();

    }

    @Override
    public List<OrderDto> getSkippedOrders() {
        return orderExternalRepository.findSkippedOrders()
                .stream()
                .map(orderExternalMapper::mapToDto)
                .toList();
    }

    @Override
    public List<OrderDto> getCompletedOrders() {
        return orderExternalRepository.findCompletedOrders()
                .stream()
                .map(orderExternalMapper::mapToDto)
                .toList();
    }

    @Override
    public List<OrderDto> getScheduledOrders() {
        return orderExternalRepository.findScheduledOrders()
                .stream()
                .map(orderExternalMapper::mapToDto)
                .toList();
    }

    /**
     * Scenario 1. User want to schedule excluded orders so they link to an existing delivery by date
     * Scenario 2. User know exact schedule date - but respective delivery is not created yet
     *
     * Excluded    (id=0) -> deliveryId-set  / rescheduleDate-null / excluded = true -> present in excluded
     * Scheduling  (id=0) -> deliveryId-set  / rescheduleDate-set  / excluded = true ->  gone from excluded (rescheduleDate!=null)
     * Rescheduled (id=1) -> deliveryId-null / rescheduleDate-set  / excluded = false -> scheduled
     * Included    (id=1) -> deliveryId-set  / rescheduleDate-set  / excluded = false - > deliveryId != null -> already included
     */
    @Transactional
    @Override
    public List<OrderDto> rescheduleSkippedOrders(RescheduleOrdersRequest rescheduleOrdersRequest) {
        List<Integer> ordersIds = rescheduleOrdersRequest.getOrderIds();
        List<OrderExternal> ordersExternals = orderExternalRepository.findAllById(ordersIds);

        ordersExternals.forEach(order -> order.setRescheduleDate(rescheduleOrdersRequest.getRescheduleDate()));
        List<OrderExternal> updatedSkippedOrders = orderExternalRepository.saveAll(ordersExternals);

        List<OrderExternal> newScheduledOrders =  updatedSkippedOrders
                .stream()
                .map(orderExternalMapper::copyNew)
                .toList();

        // If delivery already exist for specified date -> link to delivery.
        deliveryRepository.findByDeliveryDate(rescheduleOrdersRequest.getRescheduleDate())
                .ifPresent(delivery ->
                        newScheduledOrders.forEach(newScheduledOrder -> newScheduledOrder.setDelivery(delivery)));

        return orderExternalRepository.saveAll(newScheduledOrders)
                .stream()
                .map(orderExternalMapper::mapToDto)
                .toList();
    }

    @Override
    public List<OrderDto> removeExcludedOrders(RemoveOrdersRequest removeOrdersRequest) {
        List<Integer> ordersIds = removeOrdersRequest.getOrderIds();
        List<OrderExternal> ordersExternals = orderExternalRepository.findAllById(ordersIds);

        ordersExternals.forEach(order -> order.setRescheduleDate(LocalDate.EPOCH)); // Set 1970 1 1

        return orderExternalRepository.saveAll(ordersExternals)
                .stream()
                .map(orderExternalMapper::mapToDto)
                .toList();
    }
}