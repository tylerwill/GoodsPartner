package com.goodspartner.service;

import com.goodspartner.dto.OrderDto;
import com.goodspartner.web.controller.response.RoutesCalculation;

import java.util.List;

public interface CarLoadService {

    List<RoutesCalculation.CarLoadDto> map(List<RoutesCalculation.RouteDto> routes, List<OrderDto> orders);

    RoutesCalculation.CarLoadDto routeToCarDetails(RoutesCalculation.RouteDto route, List<OrderDto> orders);

}
