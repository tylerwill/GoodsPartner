package com.goodspartner.mapper;

import com.goodspartner.dto.CarDeliveryDto;
import com.goodspartner.dto.DeliveryDto;
import com.goodspartner.entity.Delivery;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring",
        uses = {RouteMapper.class, OrderExternalMapper.class, ProductShippingMapper.class, CarLoadMapper.class})
public interface DeliveryMapper {

    @Mapping(target = "id", ignore = true)
    Delivery update(@MappingTarget Delivery delivery, DeliveryDto deliveryDto);

    Delivery mapToEntity(DeliveryDto deliveryDto);

    @Mapping(target = "routes", ignore = true)
    @Mapping(target = "orders", ignore = true)
    @Mapping(target = "carLoads", ignore = true)
    CarDeliveryDto deliveryToCarDeliveryDto(Delivery delivery);

    DeliveryDto mapToDto(Delivery delivery);
}