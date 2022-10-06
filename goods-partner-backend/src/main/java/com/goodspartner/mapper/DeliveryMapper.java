package com.goodspartner.mapper;

import com.goodspartner.dto.DeliveryDto;
import com.goodspartner.dto.DeliveryShortDto;
import com.goodspartner.entity.Delivery;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring",
        uses = {RouteMapper.class, OrderExternalMapper.class, ProductShippingMapper.class})
public interface DeliveryMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "routes", ignore = true)
    @Mapping(target = "orders", ignore = true)
    @Mapping(target = "carLoads", ignore = true)
    Delivery update(@MappingTarget Delivery delivery, DeliveryDto deliveryDto);

    @Mapping(target = "routes", ignore = true)
    @Mapping(target = "orders", ignore = true)
    @Mapping(target = "productsShipping", ignore = true)
    DeliveryDto toDeliveryDtoResult(@MappingTarget DeliveryDto deliveryDto, Delivery delivery);

    @Mapping(target = "productsShipping", source = "carLoads")
    DeliveryDto deliveryToDeliveryDto(Delivery delivery);

    @Mapping(target = "carLoads", ignore = true)
    Delivery deliveryDtoToDelivery(DeliveryDto deliveryDto);

    List<DeliveryDto> deliveriesToDeliveryDtos(List<Delivery> deliveries);

    @Mapping(target = "orderCount", expression = "java(delivery.getOrders().size())")
    @Mapping(target = "routeCount", expression = "java(delivery.getRoutes().size())")
    DeliveryShortDto deliveryToDeliveryShortDto(Delivery delivery);
}