package com.goodspartner.mapper;

import com.goodspartner.dto.DeliveryHistoryDto;
import com.goodspartner.entity.DeliveryHistory;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface DeliveryHistoryMapper {

    @Mapping(target = "deliveryId", source = "delivery.id")
    DeliveryHistoryDto toDeliveryHistoryDto(DeliveryHistory deliveryHistory);

}
