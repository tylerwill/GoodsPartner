package com.goodspartner.mapper;

import com.goodspartner.dto.MapPoint;
import com.goodspartner.dto.OrderDto;
import com.goodspartner.entity.AddressExternal;
import com.goodspartner.entity.OrderExternal;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.List;


@Mapper(componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface OrderExternalMapper {

    @Mapping(target = "mapPoint", source = "mapPoint")
    @Mapping(target = "address", source = "address")
    @Mapping(target = "clientName", source = "clientName")
    @Mapping(target = "deliveryId", source = "delivery.id")
    @Mapping(target = "frozen", source = "frozen")
    OrderDto toOrderDto(OrderExternal orderExternal);

    List<OrderDto> toOrderDtosList(List<OrderExternal> orderDtos);

    OrderExternal toOrderExternal(OrderDto orderDto);

    List<OrderExternal> toOrderExternalList(List<OrderDto> orderDtos);

    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "mapPoint", source = "mapPoint")
    @Mapping(target = "deliveryType", source = "deliveryType")
    @Mapping(target = "deliveryStart", source = "deliveryStart")
    @Mapping(target = "deliveryFinish", source = "deliveryFinish")
    @Mapping(target = "excluded", source = "excluded")
    @Mapping(target = "dropped", source = "dropped")
    @Mapping(target = "frozen", source = "frozen")
    @Mapping(target = "excludeReason", source = "excludeReason")
    void update(@MappingTarget OrderExternal order, OrderDto orderDto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "excluded", ignore = true)
    @Mapping(target = "excludeReason", ignore = true)
    @Mapping(target = "dropped", ignore = true)
    @Mapping(target = "delivery", ignore = true)
    @Mapping(target = "carLoad", ignore = true)
    @Mapping(target = "routePoint", ignore = true)
    @Mapping(target = "rescheduleDate", ignore = true)
    @Mapping(target = "shippingDate", source = "rescheduleDate")
    OrderExternal copyRescheduled(OrderExternal orderExternal);

    /* Address External */
    @Mapping(target = "validAddress", source = "address")
    void updateAddressExternal(@MappingTarget AddressExternal addressExternal, MapPoint mapPoint);

}