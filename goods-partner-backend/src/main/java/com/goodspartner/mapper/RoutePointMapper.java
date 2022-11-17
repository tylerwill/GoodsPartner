package com.goodspartner.mapper;

import com.goodspartner.dto.RoutePointDto;
import com.goodspartner.entity.RoutePoint;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring",
        uses = {RouteMapper.class, OrderExternalMapper.class})

public interface RoutePointMapper {

    List<RoutePoint> toRoutePointList(List<RoutePointDto> routePointDtos);

}