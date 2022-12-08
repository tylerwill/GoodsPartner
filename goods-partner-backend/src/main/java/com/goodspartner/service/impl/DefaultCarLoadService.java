package com.goodspartner.service.impl;

import com.goodspartner.entity.CarLoad;
import com.goodspartner.entity.OrderExternal;
import com.goodspartner.entity.Route;
import com.goodspartner.entity.User;
import com.goodspartner.exception.CarNotFoundException;
import com.goodspartner.repository.CarLoadRepository;
import com.goodspartner.repository.CarRepository;
import com.goodspartner.service.CarLoadService;
import com.goodspartner.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.goodspartner.entity.User.UserRole.DRIVER;

@Service
@AllArgsConstructor
public class DefaultCarLoadService implements CarLoadService {

    private final CarRepository carRepository;

    private final UserService userService;
    private final CarLoadRepository carLoadRepository;

    @Override
    public List<CarLoad> buildCarLoad(List<Route> routes, List<OrderExternal> orders) {
        return routes.stream().map(route -> routeToCarDetails(route, orders)).toList();
    }

    @Override
    public CarLoad routeToCarDetails(Route route, List<OrderExternal> orders) {
        List<OrderExternal> carLoadOrders = route.getRoutePoints()
                .stream()
                .map(routePoint -> {
                    List<String> routeOrderNumbers = routePoint.getOrders()
                            .stream()
                            .map(OrderExternal::getOrderNumber)
                            .toList();
                    return orders
                            .stream()
                            .filter(order -> routeOrderNumbers.contains(order.getOrderNumber()))
                            .toList();
                }).flatMap(List::stream)
                .collect(Collectors.toList());

        Collections.reverse(carLoadOrders); // TODO does it really make sense now?

        CarLoad carLoad = new CarLoad();
        carLoad.setCar(route.getCar());
        carLoad.setOrders(carLoadOrders);
        return carLoad;
    }

    @Transactional(readOnly = true)
    @Override
    public List<CarLoad> findByDeliveryId(UUID deliveryId, OAuth2AuthenticationToken authentication) {
        return Optional.of(userService.findByAuthentication(authentication))
                .filter(user -> DRIVER.equals(user.getRole()))
                .map(driver -> findByDeliveryAndDriver(deliveryId, driver))
                .orElseGet(() -> carLoadRepository.findByDeliveryId(deliveryId));
    }

    @Override
    public List<CarLoad> findByDeliveryId(UUID deliveryId) {
        return carLoadRepository.findByDeliveryId(deliveryId);
    }

    private List<CarLoad> findByDeliveryAndDriver(UUID deliveryId, User driver) {
        return carRepository.findCarByDriver(driver)
                .map(car -> carLoadRepository.findByDeliveryIdAndCar(deliveryId, car))
                .orElseThrow(() -> new CarNotFoundException(driver));
    }
}
