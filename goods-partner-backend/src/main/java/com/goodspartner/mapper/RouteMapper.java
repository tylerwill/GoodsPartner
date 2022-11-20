package com.goodspartner.mapper;

import com.goodspartner.dto.RouteDto;
import com.goodspartner.entity.Route;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring",
        uses = {StoreMapper.class, RoutePointMapper.class})
public interface RouteMapper {

    @Mapping(target = "car", source = "car")
    RouteDto mapToDto(Route route);

}
