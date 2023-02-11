package com.goodspartner.service.util;

import com.goodspartner.configuration.properties.ClientBusinessProperties;
import com.goodspartner.configuration.properties.ClientBusinessProperties.Postal;
import com.goodspartner.configuration.properties.ClientBusinessProperties.PrePacking;
import com.goodspartner.configuration.properties.ClientBusinessProperties.SelfService;
import com.goodspartner.dto.MapPoint;
import com.goodspartner.dto.OrderDto;
import com.goodspartner.entity.AddressStatus;
import com.goodspartner.entity.DeliveryType;
import com.goodspartner.util.DeliveryTimeRangeParser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class ExternalOrderPostProcessor {

    private final ClientBusinessProperties clientBusinessProperties;

    public void processOrderComments(List<OrderDto> orderDtos) {
        // DeliveryType:
        checkIfPostal(orderDtos);
        checkIfSelfService(orderDtos);
        checkIfPrePacking(orderDtos);
        // Other
        checkIfCoolerCarRequired(orderDtos);
        checkMiddayDelivery(orderDtos);
        updateDeliveryTime(orderDtos);
    }

    /* Deliver Type Parser*/

    void checkIfPostal(List<OrderDto> orderDtos) {
        Postal postalProps = clientBusinessProperties.getPostal();
        orderDtos.stream()
                .filter(orderDto -> checkCommentMatchKeywords(orderDto, postalProps.getKeywords()))
                .forEach(orderDto -> {
                    log.info("Order: {} has been marked as POSTAL", orderDto.getOrderNumber());
                    orderDto.setDeliveryType(DeliveryType.POSTAL);
                    orderDto.setMapPoint(getPostProcessMapPoint(orderDto.getAddress(), postalProps.getAddress()));
                });
    }

    void checkIfSelfService(List<OrderDto> orderDtos) {
        SelfService selfServiceProps = clientBusinessProperties.getSelfService();
        orderDtos.stream()
                .filter(orderDto -> checkCommentMatchKeywords(orderDto, selfServiceProps.getKeywords()))
                .forEach(orderDto -> {
                    log.info("Order: {} has been marked as SELF_SERVICE", orderDto.getOrderNumber());
                    orderDto.setDeliveryType(DeliveryType.SELF_SERVICE);
                    orderDto.setMapPoint(getPostProcessMapPoint(orderDto.getAddress(), selfServiceProps.getAddress()));
                });
    }

    void checkIfPrePacking(List<OrderDto> orderDtos) {
        PrePacking prePackingProps = clientBusinessProperties.getPrePacking();
        orderDtos.stream()
                .filter(orderDto -> checkCommentMatchKeywords(orderDto, prePackingProps.getKeywords()))
                .forEach(orderDto -> {
                    log.info("Order: {} has been marked as PRE_PACKING", orderDto.getOrderNumber());
                    orderDto.setDeliveryType(DeliveryType.PRE_PACKING);
                    orderDto.setMapPoint(getPostProcessMapPoint(orderDto.getAddress(), prePackingProps.getAddress()));
                });
    }

    /* Car Type {arsers */

    private void checkIfCoolerCarRequired(List<OrderDto> orderDtos) {
        ClientBusinessProperties.Cooler coolerProps = clientBusinessProperties.getCooler();
        orderDtos.stream()
                .filter(orderDto -> checkCommentMatchKeywords(orderDto, coolerProps.getKeywords()))
                .forEach(orderDto -> orderDto.setFrozen(true));
    }

    /* Delivery Time Parsers */

    private void checkMiddayDelivery(List<OrderDto> orderDtos) {
        ClientBusinessProperties.MiddayDelivery middayDeliveryProps = clientBusinessProperties.getMiddayDelivery();
        orderDtos.stream()
                .filter(orderDto -> checkCommentMatchKeywords(orderDto, middayDeliveryProps.getKeywords()))
                .forEach(orderDto -> orderDto.setDeliveryFinish(LocalTime.of(13, 0)));
    }

    void updateDeliveryTime(List<OrderDto> orderDtos) {
        orderDtos.forEach(DeliveryTimeRangeParser::parseDeliveryTimeFromComment);
    }

    // --- Comment Parsers ---

    boolean checkCommentMatchKeywords(OrderDto orderDto, List<String> keywords) {
        return Optional.ofNullable(orderDto.getComment())
                .map(String::toLowerCase)
                .map(orderCommentLowerCase -> keywords.stream()
                        .map(String::toLowerCase)
                        .anyMatch(orderCommentLowerCase::contains))
                .orElse(false);
    }

    /* Util */
    private MapPoint getPostProcessMapPoint(String initialAddress, String overrideAddress) {

        return overrideAddress == null
                ? MapPoint.builder().address(initialAddress).status(AddressStatus.UNKNOWN).build()
                : MapPoint.builder().address(overrideAddress).status(AddressStatus.KNOWN).build();
    }
}

