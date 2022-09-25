package com.goodspartner.mapper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.goodspartner.entity.CarLoad;
import com.goodspartner.entity.OrderExternal;
import com.goodspartner.entity.Route;
import com.goodspartner.service.CarLoadService;
import com.goodspartner.service.impl.DefaultCarLoadService;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CarLoadServiceTest {
    private final ObjectMapper objectMapper = new ObjectMapper();

    private final CarLoadService carLoadService = new DefaultCarLoadService();

    @Test
    void mapOrdersTCar() throws JsonProcessingException {
        String responseAsString = getResponseAsString("datasets/common/mapper/car/details/dataset_orders.json");
        List<OrderExternal> orders = objectMapper.readValue(responseAsString, new TypeReference<>() {
        });

        responseAsString = getResponseAsString("datasets/common/mapper/car/details/dataset_route.json");
        Route route = objectMapper.readValue(responseAsString, new TypeReference<>() {
        });


        CarLoad carLoad = carLoadService.routeToCarDetails(route, orders);


//       TODO more assertions require
        assertEquals(orders.size(), carLoad.getOrders().size());
    }

    protected String getResponseAsString(String jsonPath) {
        URL resource = getClass().getClassLoader().getResource(jsonPath);
        try {
            return FileUtils.readFileToString(new File(resource.toURI()), StandardCharsets.UTF_8);
        } catch (IOException | URISyntaxException e) {
            throw new RuntimeException("Unable to find file: " + jsonPath);
        }
    }
}