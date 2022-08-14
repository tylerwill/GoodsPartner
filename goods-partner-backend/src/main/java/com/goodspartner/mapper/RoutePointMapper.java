package com.goodspartner.mapper;

import com.goodspartner.dto.RoutePointDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface RoutePointMapper {

    @Mapping(target = "id", ignore = true)
    RoutePointDto update(@MappingTarget RoutePointDto route, RoutePointDto routeDto);

}