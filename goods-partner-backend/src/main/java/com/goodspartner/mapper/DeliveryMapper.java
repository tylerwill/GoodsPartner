package com.goodspartner.mapper;

import com.goodspartner.dto.DeliveryDto;
import com.goodspartner.dto.DeliveryShortDto;
import com.goodspartner.entity.Delivery;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;

import java.util.List;

@Mapper(componentModel = "spring",
        uses = {RouteMapper.class, OrderExternalMapper.class, CarLoadMapper.class})
public interface DeliveryMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "routes", ignore = true)
    @Mapping(target = "orders", ignore = true)
    @Mapping(target = "carLoads", ignore = true)
    Delivery update(@MappingTarget Delivery delivery, DeliveryDto deliveryDto);

    @Mapping(target = "routes", ignore = true)
    @Mapping(target = "orders", ignore = true)
    @Mapping(target = "carLoads", ignore = true)
    DeliveryDto toDeliveryDtoResult(@MappingTarget DeliveryDto deliveryDto, Delivery delivery);

    DeliveryDto deliveryToDeliveryDto(Delivery delivery);

    Delivery deliveryDtoToDelivery(DeliveryDto deliveryDto);

    List<DeliveryDto> deliveriesToDeliveryDtos(List<Delivery> deliveries);

    @Named("deliveryToDeliveryShortDto")
    default DeliveryShortDto deliveryToDeliveryShortDto(Delivery delivery) {
        return DeliveryShortDto.builder()
                .id(delivery.getId())
                .deliveryDate(delivery.getDeliveryDate())
                .status(delivery.getStatus())
                .routeCount(delivery.getRoutes().size())
                .orderCount(delivery.getOrders().size())
                .build();
    }
}
