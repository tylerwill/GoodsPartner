package com.goodspartner.service.util;

import com.goodspartner.configuration.properties.GrandeDolceBusinessProperties;
import com.goodspartner.dto.OrderDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class DefaultOrderCommentProcessor implements OrderCommentProcessor {

    private static final String FROZEN = "заморозка";
    private static final String THIRD_PARTY = "нова пошта";
    private static final String THIRD_PARTY_BINDED = "новапошта";

    private final GrandeDolceBusinessProperties grandeDolceBusinessProperties;

    @Override
    public void processOrderComments(List<OrderDto> orderDtos) {
        setFrozen(orderDtos);
        setThirdPartyAddress(orderDtos);
    }

    private void setFrozen(List<OrderDto> orderDtos) {
        orderDtos.forEach(orderDto -> {
            if (checkOrderCommentIfFrozen(orderDto)) {
                orderDto.setFrozen(true);
            }
        });
    }

    private void setThirdPartyAddress(List<OrderDto> orderDtos) {
        String thirdPartyAddress = grandeDolceBusinessProperties.getPostalDelivery().getNovaPoshtaAddress();

        orderDtos.forEach(orderDto -> {
            if (checkOrderCommentIfToBeShippedByThirdPart(orderDto)) {
                orderDto.setAddress(thirdPartyAddress);
            }
        });
    }

    private boolean checkOrderCommentIfFrozen(OrderDto orderDto) {
        String orderComment = orderDto.getComment();
        if(orderComment == null) {
            return false;
        }
        return orderComment.toLowerCase().contains(FROZEN);
    }

    private boolean checkOrderCommentIfToBeShippedByThirdPart(OrderDto orderDto) {
        String orderComment = orderDto.getComment();
        if(orderComment == null) {
            return false;
        }
        String orderCommentLowerCase = orderComment.toLowerCase();
        return orderCommentLowerCase.contains(THIRD_PARTY) || orderCommentLowerCase.contains(THIRD_PARTY_BINDED);
    }
}
