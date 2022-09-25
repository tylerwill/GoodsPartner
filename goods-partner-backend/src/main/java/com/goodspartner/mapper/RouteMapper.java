package com.goodspartner.mapper;

import com.goodspartner.dto.RouteDto;
import com.goodspartner.entity.RoutePoint;
import com.goodspartner.entity.Route;
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
    Route routeDtoToRoute(RouteDto routeDto);

    @Mapping(target = "car", source = "car")
    @Mapping(target = "car.loadSize", source = "routePoints", qualifiedByName = "mapCarLoadSize")
    RouteDto routeToRouteDto(Route routeDto);

    @Named("mapCarLoadSize")
    default double mapCarLoadSize(List<RoutePoint> routePoints) {
        return BigDecimal.valueOf(routePoints.stream()
                .map(RoutePoint::getAddressTotalWeight)
                .collect(Collectors.summarizingDouble(w -> w)).getSum())
                .setScale(2, RoundingMode.HALF_UP).doubleValue();
    }

    @Mapping(target = "id", ignore = true)
    Route update(@MappingTarget Route route, RouteDto routeDto);

    List<RouteDto> routesToRouteDtos(List<Route> routes);

    List<Route> routeDtosToRoutes(List<RouteDto> routeDtos);

}
