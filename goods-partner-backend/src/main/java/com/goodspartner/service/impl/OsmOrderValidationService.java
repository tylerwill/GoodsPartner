package com.goodspartner.service.impl;

import com.goodspartner.dto.OrderDto;
import com.goodspartner.dto.OsmGeocodingDto;
import com.goodspartner.service.OrderValidationService;
import com.goodspartner.service.dto.OrderValidationDto;
import com.google.common.annotations.VisibleForTesting;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OsmOrderValidationService implements OrderValidationService {
    private final WebClient webClient;
    private static final String BASE_URL = "https://nominatim.openstreetmap.org/search";
    private static final String RESPONSE_FORMAT = "geojson";

    @Override
    public OrderValidationDto validateOrders(List<OrderDto> orderDtos) {

        List<OrderDto> validOrders = new ArrayList<>(0);
        List<OrderDto> inValidOrders = new ArrayList<>(0);

        orderDtos.forEach(orderDto -> {

            if (validateAddress(orderDto.getAddress())) {
                orderDto.setValidAddress(true);
                validOrders.add(orderDto);
            } else {
                inValidOrders.add(orderDto);
            }
        });

        return OrderValidationDto.builder()
                .validOrders(validOrders)
                .invalidOrders(inValidOrders)
                .build();
    }

    @VisibleForTesting
    OsmGeocodingDto getOsmGeocodingDto(String address) {
        return webClient
                .get()
                .uri(BASE_URL,
                        uri -> uri
                                .queryParam("q", address)
                                .queryParam("format", RESPONSE_FORMAT)
                                .build())
                .retrieve()
                .bodyToMono(OsmGeocodingDto.class)
                .block();
    }

    @VisibleForTesting
    boolean validateAddress(String address) {
        Object[] features = getOsmGeocodingDto(address).getFeatures();
        return features.length != 0;
    }
}