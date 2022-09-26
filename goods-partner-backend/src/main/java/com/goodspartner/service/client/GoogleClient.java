package com.goodspartner.service.client;

import com.goodspartner.exceptions.GoogleApiException;
import com.google.maps.GeoApiContext;
import com.google.maps.GeocodingApi;
import com.google.maps.errors.ApiException;
import com.google.maps.model.GeocodingResult;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class GoogleClient {

    private static final String DEFAULT_LANGUAGE = "uk-UK";
    private final GeoApiContext context;

    public GoogleClient(@Value("${google.api.key}") String googleApiKey) {
        context = new GeoApiContext.Builder()
                .apiKey(googleApiKey)
                .build();
    }

    public GeocodingResult[] getGeocodingResults(String address) {
        try {
            return GeocodingApi.geocode(context, address).language(DEFAULT_LANGUAGE).await();
        } catch (IOException | ApiException | InterruptedException e) {
            throw new GoogleApiException(e);
        }
    }

}
