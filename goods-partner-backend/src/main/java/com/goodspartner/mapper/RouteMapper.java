package com.goodspartner.mapper;

import com.goodspartner.dto.RoutePointDto;
import com.goodspartner.entity.Route;
import com.goodspartner.web.controller.response.RoutesCalculation;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface RouteMapper {
    Route routeDtoToRoute(RoutesCalculation.RouteDto routeDto);

    @Mapping(target = "car", source = "car")
    @Mapping(target = "car.loadSize", source = "routePoints", qualifiedByName = "mapCarLoadSize")
    RoutesCalculation.RouteDto routeToRouteDto(Route routeDto);

    @Named("mapCarLoadSize")
    default double mapCarLoadSize(List<RoutePointDto> routePoints) {
        return BigDecimal.valueOf(routePoints.stream()
                        .map(RoutePointDto::getAddressTotalWeight)
                        .collect(Collectors.summarizingDouble(w -> w)).getSum())
                .setScale(2, RoundingMode.HALF_UP).doubleValue();
    }

    @Mapping(target = "id", ignore = true)
    Route update(@MappingTarget Route route, RoutesCalculation.RouteDto routeDto);

    List<RoutesCalculation.RouteDto> routesToRouteDtos(List<Route> routes);

    List<Route> RouteDtosToRoutes(List<RoutesCalculation.RouteDto> routeDtos);

}
