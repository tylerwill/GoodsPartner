package com.goodspartner.service.impl;

import com.goodspartner.dto.CarDeliveryDto;
import com.goodspartner.dto.CarLoadDto;
import com.goodspartner.dto.DeliveryDto;
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
import com.goodspartner.exception.DeliveryNotFoundException;
import com.goodspartner.exception.IllegalDeliveryStatusForOperation;
import com.goodspartner.exception.UnknownAddressException;
import com.goodspartner.mapper.DeliveryMapper;
import com.goodspartner.repository.CarRepository;
import com.goodspartner.repository.DeliveryRepository;
import com.goodspartner.service.CarLoadService;
import com.goodspartner.service.DeliveryService;
import com.goodspartner.service.EventService;
import com.goodspartner.service.OrderExternalService;
import com.goodspartner.service.RouteService;
import com.goodspartner.service.UserService;
import com.goodspartner.service.util.DeliveryCalculationHelper;
import com.goodspartner.web.action.DeliveryAction;
import com.goodspartner.web.controller.response.DeliveryActionResponse;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static com.goodspartner.entity.AddressStatus.UNKNOWN;
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

    @Override
    @Transactional(readOnly = true)
    public List<DeliveryDto> findAll() {
        Sort sortByDeliveryDate = Sort.by(Sort.Direction.DESC, "deliveryDate");
        return deliveryRepository.findAll(sortByDeliveryDate)
                .stream()
                .map(deliveryMapper::mapToDto)
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

        return deliveryMapper.mapToDto(deliveryToDelete);
    }

    @Override
    @Transactional(readOnly = true)
    public DeliveryDto findById(UUID deliveryId) {
        return deliveryRepository.findById(deliveryId)
                .map(deliveryMapper::mapToDto)
                .orElseThrow(() -> new DeliveryNotFoundException(deliveryId));
    }

    @Override
    public DeliveryDto findByStatusAndDeliveryDate(DeliveryStatus status, LocalDate date) {
        Delivery delivery = deliveryRepository.findByStatusAndDeliveryDate(status, date)
                .orElseThrow(() -> new DeliveryNotFoundException(status, date));
        return deliveryMapper.mapToDto(delivery);
    }

    @Override
    public List<DeliveryDto> findByStatusAndDeliveryDateBetween(DeliveryStatus status, LocalDate dateFrom, LocalDate dateTo) {
        return deliveryRepository.findByStatusAndDeliveryDateBetween(status, dateFrom, dateTo)
                .stream()
                .map(deliveryMapper::mapToDto)
                .toList();
    }

    @Override
    @Transactional
    public Delivery add(DeliveryDto deliveryDto) {
        deliveryRepository.findByDeliveryDate(deliveryDto.getDeliveryDate())
                .ifPresent(delivery -> {
                    throw new IllegalArgumentException("There is already delivery on date: " + deliveryDto.getDeliveryDate());
                });

        deliveryDto.setStatus(DRAFT);
        deliveryDto.setFormationStatus(ORDERS_LOADING);

        return deliveryRepository.save(deliveryMapper.mapToEntity(deliveryDto));
    }

    @Override
    public DeliveryDto calculateDelivery(UUID id) {

        Delivery delivery = deliveryRepository.findById(id)
                .orElseThrow(() -> new DeliveryNotFoundException(id));

        List<OrderExternal> orders = delivery.getOrders();
        validateOrderAddresses(orders); // Do not create Delivery with UNKNOWN orders address
        resetOrders(orders); // If delivery already have order silently reset and exit for recalculation

        delivery.setFormationStatus(DeliveryFormationStatus.ROUTE_CALCULATION);

        deliveryCalculationHelper.calculate(id);  //Calculated in async method

        eventService.publishDeliveryEvent(DeliveryHistoryTemplate.DELIVERY_CALCULATED, id);

        return deliveryMapper.mapToDto(deliveryRepository.save(delivery));
    }

    private void validateOrderAddresses(List<OrderExternal> orders) {
        orders.stream()
                .map(OrderExternal::getAddressExternal)
                .filter(addressExternal -> UNKNOWN.equals(addressExternal.getStatus()))
                .findFirst()
                .ifPresent(addressExternal -> {
                    throw new UnknownAddressException(addressExternal);
                });
    }

    private void resetOrders(List<OrderExternal> orders) {
        orders.forEach(orderExternal -> {
            orderExternal.setCarLoad(null);
            orderExternal.setRoutePoint(null);
            orderExternal.setDropped(false);
        });
    }

    @Override
    @Transactional
    public DeliveryActionResponse approve(UUID id, DeliveryAction action) {

        Delivery delivery = deliveryRepository.findById(id)
                .orElseThrow(() -> new DeliveryNotFoundException(id));

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
    public List<DeliveryDto> findAll(OAuth2AuthenticationToken authentication) {
        UserDto driver = userService.findByAuthentication(authentication);
        Car car = carRepository.findCarByDriver(driver.getUserName());
        return deliveryRepository.findDeliveriesByCar(car)
                .stream()
                .map(deliveryMapper::mapToDto)
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
}
