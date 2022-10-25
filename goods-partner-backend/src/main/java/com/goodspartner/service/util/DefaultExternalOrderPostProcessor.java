package com.goodspartner.service.util;

import com.goodspartner.configuration.properties.GrandeDolceBusinessProperties;
import com.goodspartner.dto.OrderDto;
import com.goodspartner.entity.DeliveryType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

// TODO postprocess address line as well
@Component
@RequiredArgsConstructor
public class DefaultExternalOrderPostProcessor implements ExternalOrderPostProcessor {

    private static final String FROZEN = "заморозка";
    private static final String POSTAL = "нова пошта";
    private static final String POSTAL_BINDED = "новапошта";
    private static final String PRE_PACKING = "фасовка";
    private static final String SELF_SERVICE = "самовивіз";

    private final GrandeDolceBusinessProperties grandeDolceBusinessProperties;

    @Override
    public void processOrderComments(List<OrderDto> orderDtos) {
        checkIfFrozen(orderDtos);
        checkIfPostal(orderDtos);
        checkIfSelfService(orderDtos);
        checkIfPrePacking(orderDtos);
    }

    private void checkIfFrozen(List<OrderDto> orderDtos) {
        orderDtos.stream()
                .filter(this::checkCommentIfFrozen)
                .forEach(orderDto -> orderDto.setFrozen(true));
    }

    private void checkIfPostal(List<OrderDto> orderDtos) {
        orderDtos.stream()
                .filter(this::checkCommentIfShippedByPostal)
                .forEach(orderDto -> orderDto.setDeliveryType(DeliveryType.POSTAL));
    }

    private void checkIfSelfService(List<OrderDto> orderDtos) {
        orderDtos.stream()
                .filter(this::checkCommentIdSelfService)
                .forEach(orderDto -> orderDto.setDeliveryType(DeliveryType.SELF_SERVICE));
    }

    private void checkIfPrePacking(List<OrderDto> orderDtos) {
        orderDtos.stream()
                .filter(this::checkCommentIfPrePacking)
                .forEach(orderDto -> {
                    orderDto.setAddress(grandeDolceBusinessProperties.getPrePacking().getAddress());
                    orderDto.setDeliveryType(DeliveryType.PRE_PACKING);
                });
    }

    // --- Comment Parsers ---

    private boolean checkCommentIfFrozen(OrderDto orderDto) {
        return Optional.ofNullable(orderDto.getComment())
                .map(String::toLowerCase)
                .map(orderCommentLowerCase -> orderCommentLowerCase.contains(FROZEN))
                .orElse(false);
    }

    private boolean checkCommentIfShippedByPostal(OrderDto orderDto) {
        return Optional.ofNullable(orderDto.getComment())
                .map(String::toLowerCase)
                .map(orderCommentLowerCase ->
                        orderCommentLowerCase.contains(POSTAL) || orderCommentLowerCase.contains(POSTAL_BINDED))
                .orElse(false);
    }

    private boolean checkCommentIfPrePacking(OrderDto orderDto) {
        return Optional.ofNullable(orderDto.getComment())
                .map(String::toLowerCase)
                .map(orderCommentLowerCase -> orderCommentLowerCase.contains(PRE_PACKING))
                .orElse(false);
    }

    private boolean checkCommentIdSelfService(OrderDto orderDto) {
        return Optional.ofNullable(orderDto.getComment())
                .map(String::toLowerCase)
                .map(orderCommentLowerCase -> orderCommentLowerCase.contains(SELF_SERVICE))
                .orElse(false);
    }
}
