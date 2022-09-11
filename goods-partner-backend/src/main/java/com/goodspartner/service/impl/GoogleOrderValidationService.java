package com.goodspartner.service.impl;

import com.goodspartner.dto.MapPoint;
import com.goodspartner.dto.OrderDto;
import com.goodspartner.entity.AddressExternal;
import com.goodspartner.entity.util.OrderAddressId;
import com.goodspartner.repository.AddressExternalRepository;
import com.goodspartner.service.GoogleApiService;
import com.goodspartner.service.OrderValidationService;
import com.google.maps.model.GeocodingResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.goodspartner.entity.AddressStatus.AUTOVALIDATED;
import static com.goodspartner.entity.AddressStatus.KNOWN;
import static com.goodspartner.entity.AddressStatus.UNKNOWN;

@Service
@RequiredArgsConstructor
public class GoogleOrderValidationService implements OrderValidationService {

    private final GoogleApiService googleApiService;
    private final AddressExternalRepository addressExternalRepository;

    @Override
    public void enrichValidAddress(List<OrderDto> orders) {
        orders.forEach(this::enrichAddress);
    }

    private void enrichAddress(OrderDto orderDto) {
        String orderAddress = orderDto.getAddress();
        OrderAddressId externalAddressId = OrderAddressId.builder()
                .orderAddress(orderAddress)
                .clientName(orderDto.getClientName())
                .build();

        MapPoint mapPoint = addressExternalRepository.findById(externalAddressId)
                .map(this::convertToKnown)
                .orElseGet(() -> autovalidate(orderAddress));

        orderDto.setMapPoint(mapPoint);
    }

    private MapPoint convertToKnown(AddressExternal addressExternal) {
        String knownAddress = addressExternal.getValidAddress();
        double latitude = addressExternal.getLatitude();
        double longitude = addressExternal.getLongitude();
        return MapPoint.builder()
                .address(knownAddress)
                .longitude(longitude)
                .latitude(latitude)
                .status(KNOWN)
                .build();
    }

    private MapPoint autovalidate(String orderAddress) {
        GeocodingResult[] geocodingResults = googleApiService.getGeocodingResults(orderAddress);
        if (geocodingResults.length == 0) { // Nothing found
            return MapPoint.builder()
                    .status(UNKNOWN)
                    .build();
        }

        GeocodingResult geocodingResult = geocodingResults[0]; // Always get the first available result
        String geocodedAddress = geocodingResult.formattedAddress;
        double latitude = geocodingResult.geometry.location.lat;
        double longitude = geocodingResult.geometry.location.lng;
        return MapPoint.builder()
                .address(geocodedAddress)
                .longitude(longitude)
                .latitude(latitude)
                .status(AUTOVALIDATED)
                .build();
    }
}