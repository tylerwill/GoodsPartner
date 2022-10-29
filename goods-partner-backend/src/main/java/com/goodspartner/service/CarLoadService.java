package com.goodspartner.service;

import com.goodspartner.dto.CarLoadDto;
import com.goodspartner.entity.Car;
import com.goodspartner.entity.CarLoad;
import com.goodspartner.entity.Delivery;
import com.goodspartner.entity.OrderExternal;
import com.goodspartner.entity.Route;

import java.util.List;

public interface CarLoadService {

    List<CarLoadDto> findCarLoad(Delivery delivery, Car car);

    List<CarLoad> buildCarLoad(List<Route> routes, List<OrderExternal> orders);

    CarLoad routeToCarDetails(Route route, List<OrderExternal> orders);

}
