package com.goodspartner.mapper;

import com.goodspartner.dto.DeliveryDto;
import com.goodspartner.entity.Delivery;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring", uses = {RouteMapper.class})
public interface DeliveryRouteMapper {

    @Mapping(target = "orders", ignore = true)
    @Mapping(target = "carLoads", ignore = true)
    List<DeliveryDto> mapDeliveriesWithRoutes(List<Delivery> deliveries);

    @Mapping(target = "orders", ignore = true)
    @Mapping(target = "productsShipping", ignore = true)
    DeliveryDto mapDeliveryWithRoute(Delivery delivery);

}
