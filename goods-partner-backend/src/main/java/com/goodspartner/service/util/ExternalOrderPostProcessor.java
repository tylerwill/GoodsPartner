package com.goodspartner.service.util;

import com.goodspartner.configuration.properties.ClientBusinessProperties;
import com.goodspartner.dto.MapPoint;
import com.goodspartner.dto.OrderDto;
import com.goodspartner.entity.AddressStatus;
import com.goodspartner.entity.DeliveryType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class ExternalOrderPostProcessor {

    private static final List<String> POSTAL_KEYWORDS = Arrays.asList("нова пошта", "новапошта", " нп ", "делівері", "delivery", "делівери");
    private static final String FROZEN = "заморозка";
    private static final String PRE_PACKING = "фасовк"; // Include "фасовка"
    private static final String SELF_SERVICE = "самовивіз";
    private static final String MIDDAY_DELIVERY = "до обіду";

    private final ClientBusinessProperties clientBusinessProperties;

    public void processOrderComments(List<OrderDto> orderDtos) {
        checkIfFrozen(orderDtos);
        checkIfPostal(orderDtos);
        checkIfSelfService(orderDtos);
        checkIfPrePacking(orderDtos);
        checkMiddayDelivery(orderDtos);
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
                    orderDto.setDeliveryType(DeliveryType.POSTAL);
                    orderDto.setAddress(clientBusinessProperties.getPostal().getAddress());
                    orderDto.setMapPoint(getPostProcessMapPoint(clientBusinessProperties.getPostal().getAddress()));
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
                    orderDto.setDeliveryType(DeliveryType.SELF_SERVICE);
                    orderDto.setAddress(clientBusinessProperties.getSelfService().getAddress());
                    orderDto.setMapPoint(getPostProcessMapPoint(clientBusinessProperties.getSelfService().getAddress()));
                });
    }

    private void checkIfPrePacking(List<OrderDto> orderDtos) {
        orderDtos.stream()
                .filter(this::checkCommentIfPrePacking)
                .forEach(orderDto -> {
                    orderDto.setDeliveryType(DeliveryType.PRE_PACKING);
                    orderDto.setAddress(clientBusinessProperties.getPrePacking().getAddress());
                    orderDto.setMapPoint(getPostProcessMapPoint(clientBusinessProperties.getPrePacking().getAddress()));
                });
    }

    private void checkMiddayDelivery(List<OrderDto> orderDtos) {
        orderDtos.stream()
                .filter(this::checkCommentIfMiddayDelivery)
                .forEach(orderDto -> orderDto.setDeliveryFinish(LocalTime.of(13, 0)));
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
                                POSTAL_KEYWORDS.stream()
                                        .map(String::toLowerCase)
                                        .anyMatch(orderCommentLowerCase::contains))
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
    
    private boolean checkCommentIfMiddayDelivery(OrderDto orderDto) {
        return Optional.ofNullable(orderDto.getComment())
                .map(String::toLowerCase)
                .map(orderCommentLowerCase -> orderCommentLowerCase.contains(MIDDAY_DELIVERY))
                .orElse(false);
    }
}
