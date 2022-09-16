package com.goodspartner.mapper;

import com.goodspartner.dto.DeliveryDto;
import com.goodspartner.dto.OrderDto;
import com.goodspartner.entity.CarLoad;
import com.goodspartner.entity.Delivery;
import com.goodspartner.entity.OrderExternal;
import com.goodspartner.entity.Route;
import com.goodspartner.web.controller.response.RoutesCalculation;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Mapper(componentModel = "spring")
public abstract class DeliveryMapper {
    @Autowired
    private RouteMapper routeMapper;
    @Autowired
    private OrderExternalMapper orderExternalMapper;
    @Autowired
    private CarLoadMapper carLoadMapper;

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "routes", ignore = true)
    @Mapping(target = "orders", ignore = true)
    @Mapping(target = "carLoads", ignore = true)
    public abstract Delivery update(@MappingTarget Delivery delivery, DeliveryDto deliveryDto);

    @Mapping(target = "routes", ignore = true)
    @Mapping(target = "orders", ignore = true)
    @Mapping(target = "carLoads", ignore = true)
    public abstract DeliveryDto toDeliveryDtoResult(@MappingTarget DeliveryDto deliveryDto, Delivery delivery);


    @Mapping(target = "orders", source = "orders", qualifiedByName = "mapOrders")
    @Mapping(target = "routes", source = "routes", qualifiedByName = "mapRoutes")
    @Mapping(target = "carLoads", source = "carLoads", qualifiedByName = "mapCarloads")
    public abstract DeliveryDto deliveryToDeliveryDto(Delivery delivery);

    @Named("mapOrders")
    List<OrderDto> mapOrders(List<OrderExternal> orderExternals) {
        return orderExternalMapper.mapExternalOrdersToOrderDtos(orderExternals);
    }

    @Named("mapRoutes")
    List<RoutesCalculation.RouteDto> mapRoutes(List<Route> routes) {
        return routeMapper.routesToRouteDtos(routes);
    }

    @Named("mapCarloads")
    List<RoutesCalculation.CarLoadDto> mapCarLoads(List<CarLoad> carLoads) {
        return carLoadMapper.toCarLoadDtos(carLoads);
    }

    @Mapping(target = "orders", source = "orders", qualifiedByName = "mapOrdersDto")
    @Mapping(target = "routes", source = "routes", qualifiedByName = "mapRoutesDto")
    @Mapping(target = "carLoads", source = "carLoads", qualifiedByName = "mapCarloadsDto")
    public abstract Delivery deliveryDtoToDelivery(DeliveryDto deliveryDto);


    @Named("mapOrdersDto")
    List<OrderExternal> mapOrdersDto(List<OrderDto> orderDtos) {
        List<OrderDto> collect = Optional.ofNullable(orderDtos)
                .orElseGet(Collections::emptyList);
        return orderExternalMapper.mapOrderDtosToOrderExternal(collect);
    }

    @Named("mapRoutesDto")
    List<Route> mapRoutesDto(List<RoutesCalculation.RouteDto> routeDtos) {
        List<RoutesCalculation.RouteDto> collect = Optional.ofNullable(routeDtos)
                .orElseGet(Collections::emptyList);
        return routeMapper.RouteDtosToRoutes(collect);
    }

    @Named("mapCarloadsDto")
    List<CarLoad> mapCarLoadsDto(List<RoutesCalculation.CarLoadDto> carLoadDtos) {
        List<RoutesCalculation.CarLoadDto> collect = Optional.ofNullable(carLoadDtos)
                .orElseGet(Collections::emptyList);
        return carLoadMapper.toCarLoads(collect);
    }

    public abstract List<DeliveryDto> deliveriesToDeliveryDtos(List<Delivery> deliveries);
}
