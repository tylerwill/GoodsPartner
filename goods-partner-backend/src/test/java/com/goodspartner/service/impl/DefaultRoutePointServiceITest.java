package com.goodspartner.service.impl;

import com.github.database.rider.core.api.dataset.DataSet;
import com.github.database.rider.core.api.dataset.ExpectedDataSet;
import com.github.database.rider.spring.api.DBRider;
import com.goodspartner.AbstractBaseITest;
import com.goodspartner.dto.Coordinates;
import com.goodspartner.exception.DistanceOutOfLimitException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.jupiter.api.Assertions.assertThrows;

@DBRider
class DefaultRoutePointServiceITest extends AbstractBaseITest {
    @Autowired
    private DefaultRoutePointService routePointService;

    private static final long ROUTE_POINT_ID = 7559900;

    @Test
    @DataSet(value = "datasets/common/update_client_coordinates/routes_dataset.yml")
    @ExpectedDataSet(value = "datasets/common/update_client_coordinates/dataset_routes_expected.yml")
    @DisplayName("when Update Coordinates then Client Coordinates Successfully Updated")
    public void whenUpdateCoordinates_thenClientCoordinatesUpdated() {
        Coordinates coordinates = new Coordinates(50.46946, 30.50268);

        routePointService.updateCoordinates(ROUTE_POINT_ID, coordinates);
    }

    @Test
    @DataSet(value = "datasets/common/update_client_coordinates/routes_dataset.yml")
    @DisplayName("when Update Coordinates With Over Distance Limit then Throw DistanceOutOfLimitException")
    public void whenUpdateCoordinatesWithOverDistanceLimit_thenThrowDistanceOutOfLimitException() {
        Coordinates coordinates = new Coordinates(50.47017, 30.50648);

        assertThrows(DistanceOutOfLimitException.class, () -> routePointService
                .updateCoordinates(ROUTE_POINT_ID, coordinates));
    }

}