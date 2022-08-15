package com.goodspartner.mapper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.goodspartner.dto.OrderDto;
import com.goodspartner.web.controller.response.RoutesCalculation;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CarDetailsMapperTest {
    private final CarDetailsMapper carDetailsMapper = new CarDetailsMapper();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void mapOrdersTCar() throws JsonProcessingException {
        String responseAsString = getResponseAsString("datasets/common/mapper/car/details/dataset_orders.json");
        List<OrderDto> orders = objectMapper.readValue(responseAsString, new TypeReference<List<OrderDto>>() {
        });

        responseAsString = getResponseAsString("datasets/common/mapper/car/details/dataset_route.json");
        RoutesCalculation.RouteDto route = objectMapper.readValue(responseAsString, new TypeReference<>() {
        });


        RoutesCalculation.CarLoadDto carLoadDto = carDetailsMapper.routeToCarDetails(route, orders);


        assertEquals(route.getTotalWeight(), carLoadDto.getCar().getLoadSize());
        assertEquals(orders.size(), carLoadDto.getOrders().size());
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