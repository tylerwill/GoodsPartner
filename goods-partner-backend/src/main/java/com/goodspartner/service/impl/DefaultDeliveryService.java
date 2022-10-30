package com.goodspartner.service.impl;

import com.goodspartner.action.DeliveryAction;
import com.goodspartner.dto.CarDeliveryDto;
import com.goodspartner.dto.CarLoadDto;
import com.goodspartner.dto.DeliveryDto;
import com.goodspartner.dto.DeliveryShortDto;
import com.goodspartner.dto.OrderDto;
import com.goodspartner.dto.RouteDto;
import com.goodspartner.dto.UserDto;
import com.goodspartner.entity.Car;
import com.goodspartner.entity.Delivery;
import com.goodspartner.entity.DeliveryFormationStatus;
import com.goodspartner.entity.DeliveryHistoryTemplate;
import com.goodspartner.entity.DeliveryStatus;
import com.goodspartner.entity.OrderExternal;
import com.goodspartner.entity.Route;
import com.goodspartner.entity.RouteStatus;
import com.goodspartner.exception.DeliveryModifyException;
import com.goodspartner.exception.DeliveryNotFoundException;
import com.goodspartner.exception.IllegalDeliveryStatusForOperation;
import com.goodspartner.exception.NoOrdersFoundForDelivery;
import com.goodspartner.mapper.DeliveryMapper;
import com.goodspartner.repository.CarRepository;
import com.goodspartner.repository.DeliveryRepository;
import com.goodspartner.service.CarLoadService;
import com.goodspartner.service.DeliveryHistoryService;
import com.goodspartner.service.DeliveryService;
import com.goodspartner.service.EventService;
import com.goodspartner.service.OrderExternalService;
import com.goodspartner.service.RouteService;
import com.goodspartner.service.UserService;
import com.goodspartner.service.util.DeliveryCalculationHelper;
import com.goodspartner.web.controller.response.DeliveryActionResponse;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.goodspartner.entity.DeliveryFormationStatus.ORDERS_LOADED;
import static com.goodspartner.entity.DeliveryFormationStatus.ORDERS_LOADING;
import static com.goodspartner.entity.DeliveryStatus.DRAFT;

@Slf4j
@AllArgsConstructor
@Service
public class DefaultDeliveryService implements DeliveryService {

    private final DeliveryMapper deliveryMapper;
    private final DeliveryRepository deliveryRepository;
    private final CarRepository carRepository;

    private final DeliveryCalculationHelper deliveryCalculationHelper;
    private final OrderExternalService orderExternalService;
    private final EventService eventService;
    private final CarLoadService carLoadService;
    private final RouteService routeService;
    private final UserService userService;

    // TODO N+1 issue when Lazy fetching orders for each delivery
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
        DeliveryDto deliveryDto = deliveryRepository.findById(deliveryId)
                .map(deliveryMapper::deliveryToDeliveryDto)
                .orElseThrow(() -> new DeliveryNotFoundException(deliveryId));

        if (deliveryDto.getOrders().isEmpty()) {
            List<OrderDto> ordersFromCache = orderExternalService.getOrdersFromCache(deliveryId);
            if (!ordersFromCache.isEmpty()) {
                deliveryDto.setOrders(ordersFromCache);
                deliveryDto.setFormationStatus(ORDERS_LOADED);
            }
        }

        return deliveryDto;
    }

    @Override
    @Transactional
    public DeliveryDto add(DeliveryDto deliveryDto) {
        Optional<Delivery> optionalDelivery = deliveryRepository.findByDeliveryDate(deliveryDto.getDeliveryDate());
        if (optionalDelivery.isPresent()) {
            throw new IllegalArgumentException("There is already delivery on date: " + deliveryDto.getDeliveryDate());
        }

        deliveryDto.setStatus(DRAFT);
        deliveryDto.setFormationStatus(ORDERS_LOADING);

        Delivery addedDelivery = deliveryRepository.save(deliveryMapper.deliveryDtoToDelivery(deliveryDto));

        DeliveryDto addedDeliveryDto = deliveryMapper.toDeliveryDtoResult(new DeliveryDto(), addedDelivery);

        orderExternalService.saveToOrderCache(addedDelivery.getId(), addedDelivery.getDeliveryDate());

        eventService.publishDeliveryEvent(DeliveryHistoryTemplate.DELIVERY_CREATED, addedDeliveryDto.getId());

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

        eventService.publishDeliveryEvent(DeliveryHistoryTemplate.DELIVERY_UPDATED, updatedDelivery.getId());

        return deliveryMapper.toDeliveryDtoResult(new DeliveryDto(), updatedDelivery);
    }

    @Override
    public DeliveryDto calculateDelivery(DeliveryDto deliveryDto) {
        Delivery delivery = saveOrders(deliveryDto);

        delivery.setFormationStatus(DeliveryFormationStatus.ROUTE_CALCULATION);

        //Calculated in async method
        deliveryCalculationHelper.calculate(delivery.getId());

        return deliveryMapper.toDeliveryDtoWithOrders(new DeliveryDto(), deliveryRepository.save(delivery));
    }

    @Transactional
    private Delivery saveOrders(DeliveryDto deliveryDto) {
        UUID deliveryId = deliveryDto.getId();

        Delivery delivery = deliveryRepository.findById(deliveryId)
                .orElseThrow(() -> new DeliveryNotFoundException(deliveryDto.getId()));

        List<OrderExternal> orderExternals;

        if (delivery.getOrders().isEmpty()) {
            orderExternals = orderExternalService.saveValidOrdersAndEnrichKnownAddressesCache(deliveryDto);

            log.info("Orders for Delivery: {} have been saved to DB", delivery.getId());

            orderExternalService.evictCache(deliveryId);
        } else {
            orderExternals = resetOrders(delivery.getOrders());
            validateDelivery(delivery);
        }

        eventService.publishOrdersStatus(DeliveryHistoryTemplate.ORDERS_SAVED, deliveryId);

        delivery.setOrders(orderExternals);

        return delivery;
    }

    // Cleanup calculated order state in case of recalculation
    private List<OrderExternal> resetOrders(List<OrderExternal> orders) {
        orders.forEach(orderExternal -> {
            orderExternal.setCarLoad(null);
            orderExternal.setDropped(false);
        });
        return orders;
    }

    @Override
    @Transactional
    public DeliveryActionResponse approve(UUID deliveryId, DeliveryAction action) {

        Delivery delivery = deliveryRepository.findById(deliveryId)
                .orElseThrow(() -> new DeliveryNotFoundException(deliveryId));

        action.perform(delivery);

        eventService.publishDeliveryEvent(DeliveryHistoryTemplate.DELIVERY_APPROVED, delivery.getId());

        List<Route> routes = delivery.getRoutes();
        routes.forEach(route -> route.setStatus(RouteStatus.APPROVED));
        routes.forEach(eventService::publishRouteStatusChangeAuto);

        deliveryRepository.save(delivery);

        return mapDeliveryActionResponse(delivery, routes);
    }

    // TODO N+1 issue when Lazy fetching orders for each delivery
    // TODO required entity relation between Car and User
    @Transactional(readOnly = true)
    @Override
    public List<DeliveryShortDto> findAll(OAuth2AuthenticationToken authentication) {
        UserDto driver = userService.findByAuthentication(authentication);
        Car car = carRepository.findCarByDriver(driver.getUserName());
        return deliveryRepository.findDeliveriesByCar(car)
                .stream()
                .map(deliveryMapper::deliveryToDeliveryShortDto)
                .toList();
    }

    @Transactional(readOnly = true)
    @Override
    public CarDeliveryDto findById(UUID deliveryId, OAuth2AuthenticationToken authentication) {
        UserDto driver = userService.findByAuthentication(authentication);
        Car car = carRepository.findCarByDriver(driver.getUserName());

        Delivery delivery = deliveryRepository.findById(deliveryId)
                .orElseThrow(() -> new DeliveryNotFoundException(deliveryId));

        CarDeliveryDto carDelivery = deliveryMapper.deliveryToCarDeliveryDto(delivery);

        List<OrderDto> carOrders = orderExternalService.findOrdersByDeliveryAndCar(delivery, car);
        carDelivery.setOrders(carOrders);

        List<RouteDto> carRoutes = routeService.findRoutesByDeliveryAndCar(delivery, car);
        carDelivery.setRoutes(carRoutes);

        List<CarLoadDto> carLoads = carLoadService.findCarLoad(delivery, car);
        carDelivery.setCarLoads(carLoads);

        return carDelivery;
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
