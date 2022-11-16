package com.goodspartner.web.controller;

import com.goodspartner.dto.CarLoadDto;
import com.goodspartner.service.CarLoadService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "api/v1/car-loads", produces = MediaType.APPLICATION_JSON_VALUE)
public class CarLoadController {
    private final CarLoadService carLoadService;

    @PreAuthorize("hasAnyRole('ADMIN', 'LOGIST', 'DRIVER')")
    @GetMapping
    @ApiOperation(value = "value find Delivery by id",
            notes = "Provide an id to look up specific Delivery",
            response = CarLoadDto.class)
    public List<CarLoadDto> findByDeliveryId(@ApiParam(value = "ID value for the Delivery you need to retrieve", required = true)
                                             @RequestParam(name = "deliveries") UUID id) {
        return carLoadService.findByDeliveryId(id);

    }

}
