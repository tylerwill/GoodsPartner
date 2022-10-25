package com.goodspartner.web.controller;

import com.goodspartner.dto.DeliveryDto;
import com.goodspartner.dto.DeliveryHistoryDto;
import com.goodspartner.dto.DeliveryShortDto;
import com.goodspartner.service.DeliveryHistoryService;
import com.goodspartner.service.DeliveryService;
import com.goodspartner.service.OrderExternalService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/api/v1/deliveries", produces = MediaType.APPLICATION_JSON_VALUE)
public class DeliveryController {

    private final DeliveryService deliveryService;
    private final OrderExternalService orderExternalService;
    private final DeliveryHistoryService deliveryHistoryService;

    @PreAuthorize("hasAnyRole('ADMIN', 'LOGIST', 'DRIVER')")
    @GetMapping
    @ApiOperation(value = "Get all Deliveries",
            notes = "Return list of DeliveryDto",
            response = List.class)
    public List<DeliveryShortDto> getAll() {
        return deliveryService.findAll();
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'LOGIST', 'DRIVER')")
    @GetMapping("/{id}")
    @ApiOperation(value = "Find Delivery by id",
            notes = "Provide an id to look up specific delivery",
            response = DeliveryDto.class)
    public DeliveryDto getById(@ApiParam(value = "ID value for the delivery you need to retrieve", required = true)
                               @PathVariable("id") UUID id) {
        return deliveryService.findById(id);
    }

    // TODO: Should return DeliveryShortDto
    @PreAuthorize("hasAnyRole('ADMIN', 'LOGIST')")
    @PostMapping()
    @ResponseStatus(HttpStatus.ACCEPTED)
    @ApiOperation(value = "Add Delivery")
    public DeliveryDto add(@ApiParam(value = "DeliveryDto that you want to add", type = "DeliveryDto", required = true)
                           @RequestBody DeliveryDto deliveryDto) {
        return deliveryService.add(deliveryDto);
    }

    // TODO probably not in use anymore
    @PreAuthorize("hasAnyRole('ADMIN', 'LOGIST')")
    @PutMapping("/{id}")
    @ApiOperation(value = "Edit Delivery",
            notes = "Provide an id to edit up specific delivery")
    public DeliveryDto update(@ApiParam(value = "ID of edited Delivery", required = true)
                              @PathVariable UUID id,
                              @ApiParam(value = "Edited DeliveryDto", type = "DeliveryDto", required = true)
                              @RequestBody DeliveryDto deliveryDto) {
        return deliveryService.update(id, deliveryDto);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'LOGIST')")
    @DeleteMapping("/{id}")
    @ApiOperation(value = "Remove Delivery by id", notes = "Provide an id to remove up specific delivery")
    public DeliveryDto delete(@ApiParam(value = "ID value for the delivery you need to retrieve", required = true)
                              @PathVariable("id") UUID id) {
        return deliveryService.delete(id);
    }

    /**
     * Delivery Histories manipulation
     */

    @PreAuthorize("hasAnyRole('ADMIN', 'LOGIST', 'DRIVER')")
    @GetMapping("/{id}/histories")
    @ApiOperation(value = "Find Delivery History by Delivery id",
            notes = "Provide an id to look up specific delivery history",
            response = DeliveryHistoryDto.class)
    public List<DeliveryHistoryDto> getHistoriesByDeliveryId(@ApiParam(value = "ID value for the delivery which histories you need to retrieve", required = true)
                                                             @PathVariable("id") UUID id) {
        return deliveryHistoryService.findByDelivery(id);
    }
}