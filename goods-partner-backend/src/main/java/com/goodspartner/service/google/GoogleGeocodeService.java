package com.goodspartner.service.google;

import com.goodspartner.dto.MapPoint;
import com.goodspartner.dto.OrderDto;
import com.goodspartner.entity.AddressExternal;
import com.goodspartner.entity.AddressExternal.OrderAddressId;
import com.goodspartner.repository.AddressExternalRepository;
import com.goodspartner.service.client.GoogleClient;
import com.goodspartner.service.GeocodeService;
import com.google.maps.model.GeocodingResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.goodspartner.dto.MapPoint.AddressStatus.KNOWN;
import static com.goodspartner.dto.MapPoint.AddressStatus.UNKNOWN;
import static com.goodspartner.dto.MapPoint.AddressStatus.AUTOVALIDATED;

@Slf4j
@Service
@RequiredArgsConstructor
public class GoogleGeocodeService implements GeocodeService {

    private final GoogleClient googleClient;
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
        GeocodingResult[] geocodingResults = googleClient.getGeocodingResults(orderAddress);
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