package com.goodspartner.mapper;

import com.goodspartner.dto.DeliveryDto;
import com.goodspartner.entity.Delivery;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring")
public interface DeliveryMapper {

    @Mapping(target = "id", ignore = true)
    Delivery update(@MappingTarget Delivery delivery, DeliveryDto deliveryDto);

    @Mapping(target = "routes", ignore = true)
    @Mapping(target = "orders", ignore = true)
    @Mapping(target = "carLoads", ignore = true)
    DeliveryDto toDeliveryDtoResult(@MappingTarget DeliveryDto deliveryDto, Delivery delivery);

    DeliveryDto deliveryToDeliveryDto(Delivery delivery);

    Delivery deliveryDtoToDelivery(DeliveryDto deliveryDto);

    List<DeliveryDto> deliveriesToDeliveryDtos(List<Delivery> deliveries);
}
