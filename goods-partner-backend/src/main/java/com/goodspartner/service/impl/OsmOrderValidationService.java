package com.goodspartner.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.goodspartner.dto.MapPoint;
import com.goodspartner.dto.OrderDto;
import com.goodspartner.entity.OrderAddressValidationStatus;
import com.goodspartner.service.OrderValidationService;
import com.goodspartner.service.dto.OrderValidationDto;
import com.google.common.annotations.VisibleForTesting;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OsmOrderValidationService implements OrderValidationService {
    private final WebClient webClient;
    private final ObjectMapper mapper = new ObjectMapper();
    private static final String BASE_URL = "https://nominatim.openstreetmap.org/search";
    private static final String RESPONSE_FORMAT = "json";
    private static final String JSON_LATITUDE = "lat";
    private static final String JSON_LONGITUDE = "lon";
    private static final String JSON_ADDRESS = "display_name";

    @Override
    public OrderValidationDto validateOrders(List<OrderDto> orderDtos) {

        Map<Boolean, List<OrderDto>> validated = orderDtos.stream()
                .map(this::validateAddress).toList().stream()
                .collect(Collectors.partitioningBy(OrderDto::isValidAddress));

        return OrderValidationDto.builder()
                .validOrders(validated.get(true))
                .invalidOrders(validated.get(false))
                .build();
    }

    @VisibleForTesting
    String getGeocode(String address) {
        return webClient
                .get()
                .uri(BASE_URL,
                        uri -> uri
                                .queryParam("q", address)
                                .queryParam("format", RESPONSE_FORMAT)
                                .queryParam("polygon", 1)
                                .queryParam("addressdetails", 1)
                                .build())
                .retrieve()
                .bodyToMono(String.class)
                .block();
    }

    @VisibleForTesting
    OrderDto validateAddress(OrderDto orderDto) {
        try {

            String geocode = getGeocode(orderDto.getAddress());
            JsonNode geocodeNode = mapper.readTree(geocode).get(0);

            double lat = geocodeNode.get(JSON_LATITUDE).asDouble();
            double lon = geocodeNode.get(JSON_LONGITUDE).asDouble();
            String address = geocodeNode.get(JSON_ADDRESS).asText();

            MapPoint mapPoint = new MapPoint(address, lat, lon);

            orderDto.setValidationStatus(OrderAddressValidationStatus.AUTOVALIDATED);
            orderDto.setMapPoint(mapPoint);
            orderDto.setValidAddress(true);

            return orderDto;

        } catch (Exception e) {
            orderDto.setValidationStatus(OrderAddressValidationStatus.UNKNOWN);
            return orderDto;
        }
    }
}