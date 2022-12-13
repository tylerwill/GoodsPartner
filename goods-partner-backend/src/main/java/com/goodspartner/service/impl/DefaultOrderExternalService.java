package com.goodspartner.service.impl;

import com.goodspartner.dto.OrderDto;
import com.goodspartner.entity.AddressExternal;
import com.goodspartner.entity.AddressStatus;
import com.goodspartner.entity.Delivery;
import com.goodspartner.entity.DeliveryFormationStatus;
import com.goodspartner.entity.DeliveryType;
import com.goodspartner.entity.OrderExternal;
import com.goodspartner.entity.User;
import com.goodspartner.exception.CarNotFoundException;
import com.goodspartner.exception.OrderNotFoundException;
import com.goodspartner.mapper.OrderExternalMapper;
import com.goodspartner.repository.AddressExternalRepository;
import com.goodspartner.repository.CarRepository;
import com.goodspartner.repository.DeliveryRepository;
import com.goodspartner.repository.OrderExternalRepository;
import com.goodspartner.service.OrderExternalService;
import com.goodspartner.service.UserService;
import com.goodspartner.web.controller.request.RemoveOrdersRequest;
import com.goodspartner.web.controller.request.RescheduleOrdersRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.ListUtils;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.goodspartner.entity.DeliveryFormationStatus.ORDERS_LOADED;
import static com.goodspartner.entity.DeliveryFormationStatus.READY_FOR_CALCULATION;
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

    @Transactional
    @Override
    public OrderExternal update(long id, OrderDto orderDto) {
        OrderExternal order = orderExternalRepository.findById(id).orElseThrow(() -> new OrderNotFoundException(id));
        orderExternalMapper.update(order, orderDto);
        return orderExternalRepository.save(order);
    }

    @Transactional
    @Override
    public void bindExternalOrdersWithDelivery(List<OrderExternal> externalOrders, Delivery delivery) {

        Set<AddressExternal> addresses = externalOrders
                .stream()
                .map(OrderExternal::getAddressExternal)
                .collect(Collectors.toSet());
        List<AddressExternal> addressExternals = addressExternalRepository.saveAll(addresses);

        enrichManagedAddresses(externalOrders, addressExternals);

        List<OrderExternal> savedNewExternalOrders = orderExternalRepository.saveAll(externalOrders);
        List<OrderExternal> rescheduledOrders = orderExternalRepository.findByRescheduleDate(delivery.getDeliveryDate());
        List<OrderExternal> allAvailableOrdersByDate = ListUtils.union(savedNewExternalOrders, rescheduledOrders);

        delivery.setFormationStatus(getFormationStatus(allAvailableOrdersByDate));
        delivery.setOrders(allAvailableOrdersByDate);
        delivery.setOrderCount(allAvailableOrdersByDate.size());
        deliveryRepository.save(delivery);
    }

    @Override
    public void checkOrdersCompletion(Delivery delivery) {
        List<OrderExternal> incompleteOrders =
                orderExternalRepository.findRegularOrdersWithUnknownAddressByDeliveryId(delivery.getId());
        if (incompleteOrders.isEmpty()) {
            log.info("All orders has valid Addresses. Uopdate Delivery as READY_FOR_CALCULATION");
            delivery.setFormationStatus(DeliveryFormationStatus.READY_FOR_CALCULATION);
            deliveryRepository.save(delivery);
        }
    }

    private void enrichManagedAddresses(List<OrderExternal> externalOrders, List<AddressExternal> addressExternals) {
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
    }

    private DeliveryFormationStatus getFormationStatus(List<OrderExternal> orderExternals) {
        if (orderExternals.isEmpty()) {
            return ORDERS_LOADED;
        }
        boolean noneMatch = orderExternals.stream()
                .noneMatch(orderExternal ->
                        DeliveryType.REGULAR.equals(orderExternal.getDeliveryType())
                                && AddressStatus.UNKNOWN.equals(orderExternal.getAddressExternal().getStatus()));
        return noneMatch ? READY_FOR_CALCULATION : ORDERS_LOADED;
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