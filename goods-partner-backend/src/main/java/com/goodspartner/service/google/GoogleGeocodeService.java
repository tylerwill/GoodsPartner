package com.goodspartner.service.google;

import com.goodspartner.dto.MapPoint;
import com.goodspartner.dto.OrderDto;
import com.goodspartner.entity.AddressExternal.OrderAddressId;
import com.goodspartner.entity.DeliveryType;
import com.goodspartner.exception.AddressOutOfRegionException;
import com.goodspartner.exception.GoogleApiException;
import com.goodspartner.mapper.RoutePointMapper;
import com.goodspartner.repository.AddressExternalRepository;
import com.goodspartner.service.client.GoogleClient;
import com.goodspartner.service.GeocodeService;
import com.google.maps.model.GeocodingResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.goodspartner.entity.AddressStatus.AUTOVALIDATED;

@Slf4j
@Service
@RequiredArgsConstructor
public class GoogleGeocodeService implements GeocodeService {

    private static final double NORTH_REGION_BORDER = 51.53115;
    private static final double SOUTH_REGION_BORDER = 49.179171;
    private static final double EAST_REGION_BORDER = 32.160730;
    private static final double WEST_REGION_BORDER = 29.266897;

    private final RoutePointMapper routePointMapper;
    private final GoogleClient googleClient;
    private final AddressExternalRepository addressExternalRepository;

    @Override
    public void enrichValidAddressForRegularOrders(List<OrderDto> orders) {
        long startTime = System.currentTimeMillis();
        orders.stream()
                .filter(orderDto -> DeliveryType.REGULAR.equals(orderDto.getDeliveryType()))
                .forEach(this::enrichAddress);
        log.info("Addresses have been enriched in {}", System.currentTimeMillis() - startTime);
    }

    @Override
    public void validateOutOfRegion(OrderDto orderDto) {
        if (orderDto.getMapPoint() == null) {
            log.warn("MapPoint is not specified for order: {}", orderDto.getId());
            return;
        }
        MapPoint mapPoint = orderDto.getMapPoint();
        if (isInvalidRegionBoundaries(mapPoint.getLatitude(), mapPoint.getLongitude())) {
            throw new AddressOutOfRegionException(mapPoint);
        }
    }

    private void enrichAddress(OrderDto orderDto) {
        OrderAddressId externalAddressId = OrderAddressId.builder()
                .orderAddress(orderDto.getAddress())
                .clientName(orderDto.getClientName())
                .build();

        MapPoint mapPoint = addressExternalRepository.findById(externalAddressId)
                .map(routePointMapper::getMapPoint)
                .orElseGet(() -> autovalidate(orderDto));

        orderDto.setMapPoint(mapPoint);
    }

    private MapPoint autovalidate(OrderDto orderDto) {
        try {
            GeocodingResult[] geocodingResults = googleClient.getGeocodingResults(orderDto.getAddress());
            if (geocodingResults == null || geocodingResults.length == 0) { // Nothing found
                return routePointMapper.getUnknownMapPoint();
            }

            GeocodingResult geocodingResult = geocodingResults[0]; // Always get the first available result
            String geocodedAddress = geocodingResult.formattedAddress;
            double latitude = geocodingResult.geometry.location.lat;
            double longitude = geocodingResult.geometry.location.lng;

            //address outside defined area
            if (isInvalidRegionBoundaries(latitude, longitude)) {
                return routePointMapper.getUnknownMapPoint();
            }
            return MapPoint.builder()
                    .address(geocodedAddress)
                    .longitude(longitude)
                    .latitude(latitude)
                    .status(AUTOVALIDATED)
                    .build();

        } catch (GoogleApiException e) {
            log.error("Exception thrown while trying to geocode order with address: {}", orderDto.getAddress(), e);
            return routePointMapper.getUnknownMapPoint();
        }
    }

    private boolean isInvalidRegionBoundaries(double latitude, double longitude) {
        return latitude > NORTH_REGION_BORDER || latitude < SOUTH_REGION_BORDER
                || longitude > EAST_REGION_BORDER || longitude < WEST_REGION_BORDER;
    }


}