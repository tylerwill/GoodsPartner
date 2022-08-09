package com.goodspartner.mapper;

import com.goodspartner.entity.Route;
import com.goodspartner.web.controller.response.RoutesCalculation;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring")
public interface RouteMapper {
    Route routeDtoToRoute(RoutesCalculation.RouteDto routeDto);

    @Mapping(target = "id", ignore = true)
    Route update(@MappingTarget Route route, RoutesCalculation.RouteDto routeDto);

    List<RoutesCalculation.RouteDto> routesToRouteDtos(List<Route> routes);

}
