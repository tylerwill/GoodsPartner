package com.goodspartner.service.client;

import com.goodspartner.configuration.properties.GoogleGeocodeProperties;
import com.goodspartner.exception.AddressGeocodeException;
import com.google.maps.GeoApiContext;
import com.google.maps.GeocodingApi;
import com.google.maps.errors.ApiException;
import com.google.maps.model.GeocodingResult;
import com.google.maps.model.LatLng;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class GoogleClient {

    private final GeoApiContext context;
    private final GoogleGeocodeProperties googleGeocodeProperties;
    private final LatLng southWest;
    private final LatLng northEast;

    public GoogleClient(GoogleGeocodeProperties googleGeocodeProperties) {
        this.context = new GeoApiContext.Builder()
                .apiKey(googleGeocodeProperties.getApiKey())
                .build();
        this.googleGeocodeProperties = googleGeocodeProperties;
        GoogleGeocodeProperties.Boundaries boundaries = googleGeocodeProperties.getBoundaries();
        this.southWest = new LatLng(boundaries.getSouth(), boundaries.getWest());
        this.northEast = new LatLng(boundaries.getNorth(), boundaries.getEast());
    }

    public GeocodingResult[] getGeocodingResults(String address) {
        try {
            return GeocodingApi.geocode(context, address)
                    .bounds(southWest, northEast)
                    .region(googleGeocodeProperties.getRegion())
//                    .components(ComponentFilter.administrativeArea()) // TODO: місто Київ / Київська область / Київська обл.
                    .language(googleGeocodeProperties.getLanguage())
                    .await();
        } catch (IOException | ApiException | InterruptedException e) {
            throw new AddressGeocodeException(address, e);
        }
    }

}
