package com.goodspartner.web.controller;

import com.goodspartner.dto.CarLoadDto;
import com.goodspartner.mapper.CarLoadMapper;
import com.goodspartner.service.CarLoadService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "api/v1/car-loads", produces = MediaType.APPLICATION_JSON_VALUE)
public class CarLoadController {

    private final CarLoadService carLoadService;

    private final CarLoadMapper carLoadMapper;

    @PreAuthorize("hasAnyRole('ADMIN', 'LOGISTICIAN', 'DRIVER')")
    @GetMapping
    @ApiOperation(value = "Find CarLoad by delivery id",
            response = CarLoadDto.class)
    public List<CarLoadDto> findByDeliveryId(@ApiParam(value = "ID value for the Delivery you need to retrieve", required = true)
                                             @RequestParam(name = "deliveryId") UUID deliveryId,
                                             OAuth2AuthenticationToken authentication) {
        return carLoadService.findByDeliveryId(deliveryId, authentication)
                .stream()
                .map(carLoadMapper::toCarLoadDto)
                .collect(Collectors.toList());

    }

}
