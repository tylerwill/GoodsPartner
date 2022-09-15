package com.goodspartner.web.controller;

import com.goodspartner.dto.DeliveryDto;
import com.goodspartner.service.DeliveryService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/v1/deliveries")
public class DeliveryController {

    private final DeliveryService deliveryService;

    @PreAuthorize("hasAnyRole('ADMIN', 'LOGIST', 'DRIVER')")
    @GetMapping
    @ApiOperation(value = "Get all Deliveries",
            notes = "Return list of DeliveryDto",
            response = List.class)
    public List<DeliveryDto> getAll() {
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

    @PreAuthorize("hasAnyRole('ADMIN', 'LOGIST')")
    @PostMapping()
    @ApiOperation(value = "Add Delivery")
    public void add(@ApiParam(value = "DeliveryDto that you want to add", type = "DeliveryDto", required = true)
                    @RequestBody DeliveryDto deliveryDto) {
        deliveryService.add(deliveryDto);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'LOGIST')")
    @PutMapping("/{id}")
    @ApiOperation(value = "Edit Delivery", notes = "Provide an id to edit up specific delivery")
    public void update(@ApiParam(value = "ID of edited Delivery", required = true)
                       @PathVariable UUID id,
                       @ApiParam(value = "Edited DeliveryDto", type = "DeliveryDto", required = true)
                       @RequestBody DeliveryDto deliveryDto) {
        deliveryService.update(id, deliveryDto);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'LOGIST')")
    @DeleteMapping("/{id}")
    @ApiOperation(value = "Remove Delivery by id", notes = "Provide an id to remove up specific delivery")
    public void delete(@ApiParam(value = "ID value for the delivery you need to retrieve", required = true)
                       @PathVariable("id") UUID id) {
        deliveryService.delete(id);
    }

}
