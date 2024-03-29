package com.goodspartner.service;

import com.goodspartner.entity.CarLoad;
import com.goodspartner.entity.OrderExternal;
import com.goodspartner.entity.Route;

import java.util.List;
import java.util.UUID;

public interface CarLoadService {

    List<CarLoad> buildCarLoad(List<Route> routes, List<OrderExternal> orders);

    CarLoad routeToCarDetails(Route route, List<OrderExternal> orders);

    List<CarLoad> findByDeliveryId(UUID deliveryId);

}
