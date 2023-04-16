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
import com.goodspartner.event.ActionType;
import com.goodspartner.event.EventType;
import com.goodspartner.exception.AddressExternalNotFoundException;
import com.goodspartner.exception.CarNotFoundException;
import com.goodspartner.exception.DeliveryNotFoundException;
import com.goodspartner.exception.OrderNotFoundException;
import com.goodspartner.exception.delivery.IllegalDeliveryStateForOrderUpdate;
import com.goodspartner.mapper.AddressExternalMapper;
import com.goodspartner.mapper.OrderExternalMapper;
import com.goodspartner.repository.AddressExternalRepository;
import com.goodspartner.repository.CarRepository;
import com.goodspartner.repository.DeliveryRepository;
import com.goodspartner.repository.OrderExternalRepository;
import com.goodspartner.service.EventService;
import com.goodspartner.service.OrderExternalService;
import com.goodspartner.service.UserService;
import com.goodspartner.web.controller.request.ExcludeOrderRequest;
import com.goodspartner.web.controller.request.RemoveOrdersRequest;
import com.goodspartner.web.controller.request.RescheduleOrdersRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.ListUtils;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.goodspartner.entity.DeliveryFormationStatus.ORDERS_LOADED;
import static com.goodspartner.entity.DeliveryFormationStatus.READY_FOR_CALCULATION;
import static com.goodspartner.entity.User.UserRole.DRIVER;
import static com.goodspartner.event.EventMessageTemplate.DELIVERY_READY;

@RequiredArgsConstructor
@Slf4j
@Service
public class DefaultOrderExternalService implements OrderExternalService {

    private static final Sort DEFAULT_ORDER_EXTERNAL_SORT = Sort.by(Sort.Direction.DESC, "orderWeight", "orderNumber");

    private final OrderExternalMapper orderExternalMapper;
    private final AddressExternalMapper addressExternalMapper;
    // Repos
    private final OrderExternalRepository orderExternalRepository;
    private final AddressExternalRepository addressExternalRepository;
    private final DeliveryRepository deliveryRepository;
    private final CarRepository carRepository;
    // Services
    private final EventService eventService;
    private final UserService userService;

    @Transactional(readOnly = true)
    @Override
    public List<OrderExternal> getByDeliveryId(UUID deliveryId) {
        return Optional.of(userService.findByAuthentication())
                .filter(user -> DRIVER.equals(user.getRole()))
                .map(driver -> findByDeliveryAndDriver(deliveryId, driver))
                .orElseGet(() -> orderExternalRepository.findByDeliveryId(deliveryId, DEFAULT_ORDER_EXTERNAL_SORT));
    }

    private List<OrderExternal> findByDeliveryAndDriver(UUID deliveryId, User driver) {
        return carRepository.findCarByDriver(driver)
                .map(car -> orderExternalRepository.findAllByDeliveryAndCar(deliveryId, car, DEFAULT_ORDER_EXTERNAL_SORT))
                .orElseThrow(() -> new CarNotFoundException(driver));
    }

    @Transactional
    @Override
    public OrderExternal update(long id, OrderDto orderDto) {
        OrderExternal order = orderExternalRepository.findByIdWithDelivery(id)
                .orElseThrow(() -> new OrderNotFoundException(id));

        Delivery delivery = order.getDelivery();
        if (!DeliveryStatus.DRAFT.equals(delivery.getStatus())) {
            throw new IllegalDeliveryStateForOrderUpdate();
        }

        updateAddressExternal(orderDto, order);

        orderExternalMapper.update(order, orderDto);

        return orderExternalRepository.save(order);
    }

    private void updateAddressExternal(OrderDto orderDto, OrderExternal order) {
        if (orderDto.getMapPoint() != null
                && orderDto.getMapPoint().getStatus() == AddressStatus.KNOWN
                && order.getMapPoint().getStatus() == AddressStatus.UNKNOWN) {

            AddressExternal.OrderAddressId orderAddressId = AddressExternal.OrderAddressId.builder()
                    .orderAddress(order.getAddress())
                    .clientName(order.getClientName())
                    .build();

            addressExternalRepository.findById(orderAddressId)
                    .map(addressExternal -> addressExternalMapper.update(addressExternal, orderDto.getMapPoint()))
                    .map(addressExternalRepository::save)
                    .orElseThrow(() -> new AddressExternalNotFoundException(orderAddressId));
        } else {
            log.debug("Skipped respective AddressExternal update. OrderDto.MapPoint: {} and Order.MapPoint: {}",
                    orderDto.getMapPoint(), order.getMapPoint());
        }
    }

    @Transactional
    @Override
    public OrderExternal excludeOrder(long id, ExcludeOrderRequest excludeOrderRequest) {
        log.info("Excluding order with id: {}", id);
        OrderExternal order = orderExternalRepository.findByIdWithDelivery(id)
                .orElseThrow(() -> new OrderNotFoundException(id));
        order.setExcluded(true);
        order.setExcludeReason(excludeOrderRequest.getExcludeReason());
        return orderExternalRepository.save(order);
    }

    @Override
    public List<OrderExternal> getInvalidOrdersForCalculation(UUID deliveryId) {
        return orderExternalRepository.findOrdersForCalculation(deliveryId)
                .stream()
                .filter(orderExternal -> AddressStatus.UNKNOWN.equals(orderExternal.getMapPoint().getStatus()))
                .collect(Collectors.toList());
    }

    @Transactional
    @Override
    public void bindExternalOrdersWithDelivery(List<OrderExternal> externalOrders, UUID deliverId) {
        Delivery delivery = deliveryRepository.findById(deliverId)
                .orElseThrow(() -> new DeliveryNotFoundException(deliverId));

        // Fetching rescheduled orders
        List<OrderExternal> rescheduledOrders = orderExternalRepository.findScheduledOrdersByShippingDate(delivery.getDeliveryDate());

        List<OrderExternal> savedNewExternalOrders = orderExternalRepository.saveAll(externalOrders);
        List<OrderExternal> allAvailableOrdersByDate = ListUtils.union(savedNewExternalOrders, rescheduledOrders);

        delivery.setFormationStatus(getFormationStatus(allAvailableOrdersByDate));
        delivery.setOrders(allAvailableOrdersByDate);
        delivery.setOrderCount(allAvailableOrdersByDate.size());
        deliveryRepository.save(delivery);
    }

    // No transaction required. In case if yes -> extract live even propagation to Facade
    @Override
    public void checkDeliveryReadiness(Delivery delivery) {
        if (!ORDERS_LOADED.equals(delivery.getFormationStatus()) || // NOT ORDERS_LOADED
                READY_FOR_CALCULATION.equals(delivery.getFormationStatus()) // OR already READY_FOR_CALCULATION
        ) {
            log.trace("Delivery: {} skip READY_FOR_CALCULATION update ", delivery);
            return;
        }

        List<OrderExternal> incompleteOrders = getInvalidOrdersForCalculation(delivery.getId());
        if (incompleteOrders.isEmpty()) {
            log.info("All orders has valid Addresses. Updating Delivery as READY_FOR_CALCULATION");
            delivery.setFormationStatus(DeliveryFormationStatus.READY_FOR_CALCULATION);
            deliveryRepository.save(delivery);
            // Publishing refresh event to FE
            eventService.publishForLogist(DELIVERY_READY.getTemplate(), EventType.INFO, ActionType.DELIVERY_UPDATED, delivery.getId());
        }
    }

    @Transactional
    @Override
    public void cleanupOrders(UUID deliveryId) {
        orderExternalRepository.removeCRMOrdersByDeliveryId(deliveryId);

        List<OrderExternal> remainingRescheduledOrders = orderExternalRepository.findByDeliveryId(deliveryId, Sort.unsorted());
        remainingRescheduledOrders.forEach(rescheduledOrder -> rescheduledOrder.setDelivery(null));

        log.info("Unbounded: {} orders from delivery: {}", remainingRescheduledOrders.size(), deliveryId);
    }

    private DeliveryFormationStatus getFormationStatus(List<OrderExternal> orderExternals) {
        if (orderExternals.isEmpty()) {
            return ORDERS_LOADED;
        }
        boolean noneMatch = orderExternals.stream()
                .noneMatch(orderExternal ->
                        DeliveryType.REGULAR.equals(orderExternal.getDeliveryType())
                                && AddressStatus.UNKNOWN.equals(orderExternal.getMapPoint().getStatus()));
        return noneMatch ? READY_FOR_CALCULATION : ORDERS_LOADED;
    }

    @Transactional(readOnly = true)
    @Override
    public List<OrderExternal> getSkippedOrders() {
        return orderExternalRepository.findSkippedOrders(DEFAULT_ORDER_EXTERNAL_SORT);
    }

    @Transactional(readOnly = true)
    @Override
    public List<OrderExternal> getCompletedOrders() {
        return orderExternalRepository.findCompletedOrders(DEFAULT_ORDER_EXTERNAL_SORT);
    }

    @Transactional(readOnly = true)
    @Override
    public List<OrderExternal> getScheduledOrders() {
        return orderExternalRepository.findScheduledOrders(DEFAULT_ORDER_EXTERNAL_SORT);
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
                .map(orderExternalMapper::copyRescheduled)
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
        log.info("Dropping orders: {}", removeOrdersRequest);
        List<Long> ordersIds = removeOrdersRequest.getOrderIds();
        List<OrderExternal> ordersExternals = orderExternalRepository.findByOrderIds(ordersIds);
        ordersExternals.forEach(order -> order.setRescheduleDate(LocalDate.EPOCH)); // Set 1970 1 1
        return ordersExternals;
    }
}