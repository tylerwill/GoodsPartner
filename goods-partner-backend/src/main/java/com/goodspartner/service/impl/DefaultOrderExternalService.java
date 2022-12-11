package com.goodspartner.service.impl;

import com.goodspartner.dto.OrderDto;
import com.goodspartner.entity.AddressExternal;
import com.goodspartner.entity.AddressStatus;
import com.goodspartner.entity.Delivery;
import com.goodspartner.entity.DeliveryFormationStatus;
import com.goodspartner.entity.DeliveryStatus;
import com.goodspartner.entity.DeliveryType;
import com.goodspartner.entity.OrderExternal;
import com.goodspartner.entity.User;
import com.goodspartner.event.Action;
import com.goodspartner.event.ActionType;
import com.goodspartner.event.EventType;
import com.goodspartner.event.LiveEvent;
import com.goodspartner.exception.CarNotFoundException;
import com.goodspartner.exception.IllegalDeliveryStatusForOperation;
import com.goodspartner.exception.OrderNotFoundException;
import com.goodspartner.mapper.OrderExternalMapper;
import com.goodspartner.repository.AddressExternalRepository;
import com.goodspartner.repository.CarRepository;
import com.goodspartner.repository.DeliveryRepository;
import com.goodspartner.repository.OrderExternalRepository;
import com.goodspartner.service.EventService;
import com.goodspartner.service.GeocodeService;
import com.goodspartner.service.IntegrationService;
import com.goodspartner.service.OrderExternalService;
import com.goodspartner.service.UserService;
import com.goodspartner.service.util.ExternalOrderPostProcessor;
import com.goodspartner.web.controller.request.RemoveOrdersRequest;
import com.goodspartner.web.controller.request.RescheduleOrdersRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.ListUtils;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
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

import static com.goodspartner.entity.DeliveryHistoryTemplate.ORDERS_LOADED;
import static com.goodspartner.entity.DeliveryHistoryTemplate.ORDERS_LOADING;
import static com.goodspartner.entity.User.UserRole.DRIVER;

@RequiredArgsConstructor
@Slf4j
@Service
public class DefaultOrderExternalService implements OrderExternalService {

    private final OrderExternalMapper orderExternalMapper;

    private final OrderExternalRepository orderExternalRepository;
    private final AddressExternalRepository addressExternalRepository;
    private final DeliveryRepository deliveryRepository;
    private final CarRepository carRepository;

    private final UserService userService;
    private final IntegrationService integrationService; // GrangeDolceIntegration
    private final GeocodeService geocodeService;
    private final ExternalOrderPostProcessor orderCommentProcessor;
    private final EventService eventService;

    @Transactional(readOnly = true)
    @Override
    public List<OrderExternal> getByDeliveryId(UUID deliveryId, OAuth2AuthenticationToken authentication) {
        return Optional.of(userService.findByAuthentication(authentication))
                .filter(user -> DRIVER.equals(user.getRole()))
                .map(driver -> findByDeliveryAndDriver(deliveryId, driver))
                .orElseGet(() -> orderExternalRepository.findByDeliveryId(deliveryId));
    }

    private List<OrderExternal> findByDeliveryAndDriver(UUID deliveryId, User driver) {
        return carRepository.findCarByDriver(driver)
                .map(car -> orderExternalRepository.findAllByDeliveryAndCar(deliveryId, car))
                .orElseThrow(() -> new CarNotFoundException(driver));
    }

    @Override // Should not be Transactional in order to use READ_COMMITTED isolation level to retrieve delivery orders state
    public OrderExternal update(long id, OrderDto orderDto) {
        log.info("Updating order with id: {}", id);
        geocodeService.validateOurOfRegion(orderDto);

        OrderExternal order = orderExternalRepository.findById(id).orElseThrow(() -> new OrderNotFoundException(id));
        Delivery delivery = order.getDelivery();
        if (!DeliveryStatus.DRAFT.equals(delivery.getStatus())) {
            throw new IllegalDeliveryStatusForOperation(delivery, "update order for");
        }

        orderExternalMapper.update(order, orderDto);
        OrderExternal savedOrder = orderExternalRepository.save(order);

        if (isRegularOrdersValid(delivery)) {
            delivery.setFormationStatus(DeliveryFormationStatus.READY_FOR_CALCULATION);
            deliveryRepository.save(delivery);
        }
        return savedOrder;
    }

    @Override
    @Async("goodsPartnerThreadPoolTaskExecutor")
    @Transactional
    public void saveOrdersForDeliveryAsync(Delivery delivery) {
        UUID deliveryId = delivery.getId();
        LocalDate deliveryDate = delivery.getDeliveryDate();

        log.info("Fetching orders from 1C");
        try {
            eventService.publishOrdersStatus(ORDERS_LOADING, deliveryId);

            List<OrderExternal> scheduledOrders = orderExternalRepository.findByRescheduleDate(deliveryDate);
            List<OrderExternal> externalOrders = findExternalOrders(deliveryDate);

            List<OrderExternal> allOrders = ListUtils.union(externalOrders, scheduledOrders);
            delivery.setFormationStatus(isRegularOrdersValid(allOrders)
                    ? DeliveryFormationStatus.READY_FOR_CALCULATION
                    : DeliveryFormationStatus.ORDERS_LOADED);
            delivery.setOrders(allOrders);
            deliveryRepository.save(delivery);

            eventService.publishOrdersStatus(ORDERS_LOADED, deliveryId);
            log.info("Saved orders for delivery {} on {} deliveryDate", deliveryId, deliveryDate);

        } catch (Exception exception) {
            eventService.publishEvent(new LiveEvent("Помилка під час вивантаження замовлень з 1С",
                    EventType.ERROR, new Action(ActionType.INFO, deliveryId)));
            log.error("Failed to save orders to cache for delivery by date: {}", deliveryDate, exception);
            throw new RuntimeException(exception);
        }
    }

    private List<OrderExternal> findExternalOrders(LocalDate deliveryDate) {
        List<OrderDto> orderDtos = integrationService.findAllByShippingDate(deliveryDate);
        if (orderDtos.isEmpty()) {
            log.warn("No orders where found in 1C for deliveryDate: {}", deliveryDate);
            return Collections.emptyList();
        }

        orderCommentProcessor.processOrderComments(orderDtos);

        geocodeService.enrichValidAddressForRegularOrders(orderDtos);

        return saveValidOrdersAndEnrichAddresses(orderDtos);
    }

    private boolean isRegularOrdersValid(Delivery delivery) {
        return orderExternalRepository.findRegularOrdersWithUnknownAddressByDeliveryId(delivery.getId()).isEmpty();
    }

    private boolean isRegularOrdersValid(List<OrderExternal> orderExternals) {
        if (orderExternals.isEmpty()) {
            return false;
        }
        return orderExternals.stream()
                .noneMatch(orderExternal ->
                        DeliveryType.REGULAR.equals(orderExternal.getDeliveryType())
                                && AddressStatus.UNKNOWN.equals(orderExternal.getAddressExternal().getStatus()));
    }

    private List<OrderExternal> saveValidOrdersAndEnrichAddresses(List<OrderDto> orderDtos) {

        List<OrderExternal> externalOrders = orderDtos.stream()
                .map(orderExternalMapper::toOrderExternal)
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

    @Transactional(readOnly = true)
    @Override
    public List<OrderExternal> getSkippedOrders() {
        return orderExternalRepository.findSkippedOrders();
    }

    @Transactional(readOnly = true)
    @Override
    public List<OrderExternal> getCompletedOrders() {
        return orderExternalRepository.findCompletedOrders();
    }

    @Transactional(readOnly = true)
    @Override
    public List<OrderExternal> getScheduledOrders() {
        return orderExternalRepository.findScheduledOrders();
    }

    /**
     * Scenario 1. User want to schedule excluded orders so they link to an existing delivery by date
     * Scenario 2. User know exact schedule date - but respective delivery is not created yet
     * <p>
     * Excluded    (id=0) -> deliveryId-set  / rescheduleDate-null / excluded = true -> present in excluded
     * Scheduling  (id=0) -> deliveryId-set  / rescheduleDate-set  / excluded = true ->  gone from excluded (rescheduleDate!=null)
     * Rescheduled (id=1) -> deliveryId-null / rescheduleDate-set  / excluded = false -> scheduled
     * Included    (id=1) -> deliveryId-set  / rescheduleDate-set  / excluded = false - > deliveryId != null -> already included
     */
    @Transactional
    @Override
    public List<OrderExternal> rescheduleSkippedOrders(RescheduleOrdersRequest rescheduleOrdersRequest) {
        List<Long> ordersIds = rescheduleOrdersRequest.getOrderIds();
        List<OrderExternal> ordersExternals = orderExternalRepository.findByOrderIds(ordersIds);

        ordersExternals.forEach(order -> order.setRescheduleDate(rescheduleOrdersRequest.getRescheduleDate()));
        List<OrderExternal> updatedSkippedOrders = orderExternalRepository.saveAll(ordersExternals);

        List<OrderExternal> newScheduledOrders = updatedSkippedOrders
                .stream()
                .map(orderExternalMapper::copyNew)
                .toList();

        // If delivery already exist for specified date -> link to delivery.
        deliveryRepository.findByDeliveryDate(rescheduleOrdersRequest.getRescheduleDate())
                .ifPresent(delivery ->
                        newScheduledOrders.forEach(newScheduledOrder -> newScheduledOrder.setDelivery(delivery)));

        return orderExternalRepository.saveAll(newScheduledOrders);
    }

    @Transactional
    @Override
    public List<OrderExternal> removeExcludedOrders(RemoveOrdersRequest removeOrdersRequest) {
        List<Long> ordersIds = removeOrdersRequest.getOrderIds();
        List<OrderExternal> ordersExternals = orderExternalRepository.findByOrderIds(ordersIds);
        ordersExternals.forEach(order -> order.setRescheduleDate(LocalDate.EPOCH)); // Set 1970 1 1
        return ordersExternals;
    }
}