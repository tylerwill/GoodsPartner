package com.goodspartner.service.impl;

import com.goodspartner.exceptions.GoogleApiException;
import com.goodspartner.service.GoogleApiService;
import com.google.maps.DirectionsApi;
import com.google.maps.DistanceMatrixApi;
import com.google.maps.DistanceMatrixApiRequest;
import com.google.maps.GeoApiContext;
import com.google.maps.errors.ApiException;
import com.google.maps.model.DirectionsRoute;
import com.google.maps.model.DistanceMatrix;
import com.google.maps.model.TravelMode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

@Slf4j
@Service
public class DefaultGoogleApiService implements GoogleApiService {
    private static final String DEFAULT_LANGUAGE = "uk-UK";
    private final GeoApiContext context;

    public DefaultGoogleApiService(@Value("${google.api.key}") String GOOGLE_API_KEY) {
        context = new GeoApiContext.Builder()
                .apiKey(GOOGLE_API_KEY)
                .build();
    }


    @Override
    public DirectionsRoute getDirectionRoute(String startPoint, List<String> pointsAddresses) {
        try {
            return DirectionsApi.newRequest(context)
                    .origin(startPoint)
                    .destination(startPoint)
                    .waypoints(pointsAddresses.toArray(String[]::new))
                    .mode(TravelMode.DRIVING)
                    .departureTimeNow()
                    .await()
                    .routes[0];
        } catch (InterruptedException e) {
            log.warn("Interrupted!", e);
            Thread.currentThread().interrupt();
            throw new AssertionError(e);
        } catch (IOException | ApiException e) {
            throw new GoogleApiException(e);
        }
    }

    @Override
    public DistanceMatrix getDistanceMatrix(List<String> pointsAddresses) {
        try {
            DistanceMatrixApiRequest matrixApiRequest = DistanceMatrixApi.newRequest(context);
            return matrixApiRequest.origins(pointsAddresses.toArray(String[]::new))
                    .destinations(pointsAddresses.toArray(String[]::new))
                    .mode(TravelMode.DRIVING)
                    .language(DEFAULT_LANGUAGE)
                    .await();
        } catch (InterruptedException e) {
            log.warn("Interrupted!", e);
            Thread.currentThread().interrupt();
            throw new AssertionError(e);
        } catch (IOException | ApiException e) {
            throw new GoogleApiException(e);
        }
    }
}
