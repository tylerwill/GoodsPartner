package com.goodspartner.service.util;

import com.goodspartner.configuration.properties.GrandeDolceBusinessProperties;
import com.goodspartner.dto.MapPoint;
import com.goodspartner.dto.OrderDto;
import com.goodspartner.entity.AddressStatus;
import com.goodspartner.entity.DeliveryType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class ExternalOrderPostProcessor {

    private static final String FROZEN = "заморозка";

    private static final String POSTAL = "нова пошта";
    private static final String POSTAL_BINDED = "новапошта";
    private static final String POSTAL_SHORT = " НП ";

    private static final String PRE_PACKING = "фасовк"; // Include "фасовка"
    private static final String SELF_SERVICE = "самовивіз";

    private final GrandeDolceBusinessProperties grandeDolceBusinessProperties;

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
                .forEach(orderDto -> {
                    orderDto.setDeliveryType(DeliveryType.POSTAL.toString());
                    orderDto.setAddress(grandeDolceBusinessProperties.getPostal().getAddress());
                    orderDto.setMapPoint(getPostProcessMapPoint(grandeDolceBusinessProperties.getPostal().getAddress()));
                });
    }

    private MapPoint getPostProcessMapPoint(String postProcessorAddress) {
        return MapPoint.builder()  // TODO fetch from static data constants / etc. Enrich properties with respective longtitude latitude
                .address(postProcessorAddress)
                .status(AddressStatus.KNOWN)
                .build();
    }

    private void checkIfSelfService(List<OrderDto> orderDtos) {
        orderDtos.stream()
                .filter(this::checkCommentIdSelfService)
                .forEach(orderDto -> {
                    orderDto.setDeliveryType(DeliveryType.SELF_SERVICE.toString());
                    orderDto.setAddress(grandeDolceBusinessProperties.getSelfService().getAddress());
                    orderDto.setMapPoint(getPostProcessMapPoint(grandeDolceBusinessProperties.getSelfService().getAddress()));
                });
    }

    private void checkIfPrePacking(List<OrderDto> orderDtos) {
        orderDtos.stream()
                .filter(this::checkCommentIfPrePacking)
                .forEach(orderDto -> {
                    orderDto.setDeliveryType(DeliveryType.PRE_PACKING.toString());
                    orderDto.setAddress(grandeDolceBusinessProperties.getPrePacking().getAddress());
                    orderDto.setMapPoint(getPostProcessMapPoint(grandeDolceBusinessProperties.getPrePacking().getAddress()));
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
                        orderCommentLowerCase.contains(POSTAL)
                                || orderCommentLowerCase.contains(POSTAL_BINDED)
                                || orderCommentLowerCase.contains(POSTAL_SHORT))
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
