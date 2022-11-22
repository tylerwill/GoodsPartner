package com.goodspartner.web.controller;

import com.goodspartner.dto.DeliveryHistoryDto;
import com.goodspartner.mapper.DeliveryHistoryMapper;
import com.goodspartner.service.DeliveryHistoryService;
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
import java.util.stream.Collectors;


@RestController
@RequiredArgsConstructor
@RequestMapping(value = "api/v1/histories", produces = MediaType.APPLICATION_JSON_VALUE)
public class DeliveryHistoryController {

    private final DeliveryHistoryService deliveryHistoryService;

    private final DeliveryHistoryMapper deliveryHistoryMapper;

    @PreAuthorize("hasAnyRole('ADMIN', 'LOGIST', 'DRIVER')")
    @GetMapping
    @ApiOperation(value = "value find Delivery by id",
            notes = "Provide an id to look up specific Delivery",
            response = DeliveryHistoryDto.class)
    public List<DeliveryHistoryDto> findByDeliveryId(@ApiParam(value = "ID value for the Delivery you need to retrieve", required = true)
                                                     @RequestParam(name = "deliveryId") UUID id) {
        return deliveryHistoryService.findByDeliveryId(id)
                .stream()
                .map(deliveryHistoryMapper::mapToDto)
                .collect(Collectors.toList());
    }

}
