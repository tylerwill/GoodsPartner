package com.goodspartner.mapper;

import com.goodspartner.entity.RoutePoint;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface RoutePointMapper {

    @Mapping(target = "id", ignore = true)
    RoutePoint update(@MappingTarget RoutePoint route, RoutePoint routeDto);

}