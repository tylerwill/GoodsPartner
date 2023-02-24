package com.goodspartner.service.google;

import com.goodspartner.configuration.properties.GoogleGeocodeProperties;
import com.goodspartner.configuration.properties.GoogleGeocodeProperties.Boundaries;
import com.goodspartner.dto.MapPoint;
import com.goodspartner.dto.OrderDto;
import com.goodspartner.entity.AddressExternal.OrderAddressId;
import com.goodspartner.entity.DeliveryType;
import com.goodspartner.exception.AddressGeocodeException;
import com.goodspartner.exception.AddressOutOfRegionException;
import com.goodspartner.mapper.RoutePointMapper;
import com.goodspartner.repository.AddressExternalRepository;
import com.goodspartner.service.GeocodeService;
import com.goodspartner.service.client.GoogleClient;
import com.google.maps.model.GeocodingResult;
import com.google.maps.model.LocationType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

import static com.goodspartner.entity.AddressStatus.AUTOVALIDATED;
import static com.goodspartner.entity.AddressStatus.UNKNOWN;

@Slf4j
@Service
@RequiredArgsConstructor
public class GoogleGeocodeService implements GeocodeService {

    // TODO check if GEOMETRIC_CENTER is ok to pass
    private static final List<LocationType> INVALID_LOCATION_TYPES =
            List.of(LocationType.APPROXIMATE, LocationType.UNKNOWN);

    private final RoutePointMapper routePointMapper;
    private final GoogleClient googleClient;
    private final AddressExternalRepository addressExternalRepository;
    private final GoogleGeocodeProperties googleGeocodeProperties;

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
        if (UNKNOWN.equals(mapPoint.getStatus())) {
            log.debug("Skip address and coordinates validation for UNKNOWN mapPoint, order: {}", orderDto);
            return;
        }
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
        if (StringUtils.isEmpty(orderDto.getAddress())) {
            log.warn("Skipping address geocode. Empty address for order: {}", orderDto);
            return routePointMapper.getUnknownMapPoint();
        }

        try {
            GeocodingResult[] geocodingResults = googleClient.getGeocodingResults(orderDto.getAddress());
            if (geocodingResults == null || geocodingResults.length == 0) { // Nothing found
                log.warn("No geocodingResults returned for address: {}", orderDto.getAddress());
                return routePointMapper.getUnknownMapPoint();
            }

            return Arrays.stream(geocodingResults)
                    .filter(geocodingResult -> !INVALID_LOCATION_TYPES.contains(geocodingResult.geometry.locationType))
                    .findFirst()
                    .map(this::mapValidGeocodingResult)
                    .orElseGet(() -> mapInvalidGeocodingResults(geocodingResults, orderDto));

        } catch (AddressGeocodeException e) {
            log.error("Exception thrown while trying to geocode order with address: {}", orderDto.getAddress(), e);
            return routePointMapper.getUnknownMapPoint();
        }
    }

    private MapPoint mapValidGeocodingResult(GeocodingResult geocodingResult) {
        String geocodedAddress = geocodingResult.formattedAddress;
        double latitude = geocodingResult.geometry.location.lat;
        double longitude = geocodingResult.geometry.location.lng;

        //address outside defined area
        if (isInvalidRegionBoundaries(latitude, longitude)) {
            log.warn("Address: {} is out of delivery area", geocodingResult.formattedAddress);
            return routePointMapper.getUnknownMapPoint();
        }

        log.debug("Valid geocode - {}", geocodingResult);

        return MapPoint.builder()
                .address(geocodedAddress)
                .longitude(longitude)
                .latitude(latitude)
                .status(AUTOVALIDATED)
                .build();
    }

    private MapPoint mapInvalidGeocodingResults(GeocodingResult[] geocodingResults, OrderDto orderDto) {
        log.debug("No valid geocodingResults for address: {}", orderDto.getAddress());
        Arrays.stream(geocodingResults).forEach(gr -> log.debug("- {}", gr));
        return routePointMapper.getUnknownMapPoint();
    }

    public boolean isInvalidRegionBoundaries(double latitude, double longitude) {
        Boundaries boundaries = googleGeocodeProperties.getBoundaries();
        return latitude > boundaries.getNorth() || latitude < boundaries.getSouth()
                || longitude > boundaries.getEast() || longitude < boundaries.getWest();
    }

}