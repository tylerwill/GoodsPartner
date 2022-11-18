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

import static com.goodspartner.entity.AddressStatus.KNOWN;
import static com.goodspartner.entity.AddressStatus.UNKNOWN;
import static com.goodspartner.entity.AddressStatus.AUTOVALIDATED;

@Slf4j
@Service
@RequiredArgsConstructor
public class GoogleGeocodeService implements GeocodeService {

    private static final double NORTH_REGION_BORDER = 51.53115;
    private static final double SOUTH_REGION_BORDER = 49.179171;
    private static final double EAST_REGION_BORDER = 32.160730;
    private static final double WEST_REGION_BORDER = 29.266897;

    private final GoogleClient googleClient;
    private final AddressExternalRepository addressExternalRepository;

    @Override
    public void enrichValidAddress(List<OrderDto> orders) {
        long startTime = System.currentTimeMillis();
        orders.forEach(this::enrichAddress);
        log.info("Addresses have been enriched in {}", System.currentTimeMillis() - startTime);
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

        //address outside defined area
        if (latitude > NORTH_REGION_BORDER
            || latitude < SOUTH_REGION_BORDER
            || longitude > EAST_REGION_BORDER
            || longitude < WEST_REGION_BORDER) {
            return MapPoint.builder()
                    .status(UNKNOWN)
                    .build();
        }

        return MapPoint.builder()
                .address(geocodedAddress)
                .longitude(longitude)
                .latitude(latitude)
                .status(AUTOVALIDATED)
                .build();
    }
}