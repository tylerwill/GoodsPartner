package com.goodspartner.mapper;

import com.goodspartner.dto.DeliveryHistoryDto;
import com.goodspartner.entity.DeliveryHistory;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface DeliveryHistoryMapper {

    @Mapping(target = "delivery.id", source = "deliveryId")
    DeliveryHistory toDeliveryHistory(DeliveryHistoryDto deliveryHistoryDto);

    @Mapping(target = "deliveryId", source = "delivery.id")
    DeliveryHistoryDto toDeliveryHistoryDto(DeliveryHistory deliveryHistory);

    List<DeliveryHistoryDto> toDeliveryHistoryDtos(List<DeliveryHistory> deliveryHistories);

}
