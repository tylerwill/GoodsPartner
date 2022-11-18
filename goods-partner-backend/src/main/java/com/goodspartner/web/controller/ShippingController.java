package com.goodspartner.web.controller;

import com.goodspartner.dto.ProductShippingDto;
import com.goodspartner.service.ShippingService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "api/v1/shipping", produces = MediaType.APPLICATION_JSON_VALUE)
public class ShippingController {

    private final ShippingService shippingService;

    @PreAuthorize("hasAnyRole('ADMIN', 'LOGIST', 'DRIVER')")
    @GetMapping
    @ApiOperation(value = "value find Delivery by id",
            notes = "Provide an id to look up specific Delivery",
            response = ProductShippingDto.class)
    public List<ProductShippingDto> findByDeliveryId(@ApiParam(value = "ID value for the Delivery you need to retrieve", required = true)
                                                     @RequestParam(value = "deliveries") UUID id) {
        return shippingService.findByDeliveryId(id);

    }
}