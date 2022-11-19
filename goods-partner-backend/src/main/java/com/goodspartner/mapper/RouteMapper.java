package com.goodspartner.mapper;

import com.goodspartner.dto.RouteDto;
import com.goodspartner.entity.Route;
import com.goodspartner.entity.RoutePoint;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring",
        uses = {StoreMapper.class, RoutePointMapper.class})
public interface RouteMapper {

    @Mapping(target = "car", source = "car")
    @Mapping(target = "car.loadSize", source = "routePoints", qualifiedByName = "mapCarLoadSize")
    RouteDto mapToDto(Route route);

    List<RouteDto> toDtos(List<Route> routes);

    @Named("mapCarLoadSize")
    default double mapCarLoadSize(List<RoutePoint> routePoints) {
        return BigDecimal.valueOf(routePoints.stream()
                        .map(RoutePoint::getAddressTotalWeight)
                        .collect(Collectors.summarizingDouble(w -> w)).getSum())
                .setScale(2, RoundingMode.HALF_UP).doubleValue();
    }

}
