package com.goodspartner.web.controller;

import com.goodspartner.dto.DeliveryDto;
import com.goodspartner.dto.OrderDto;
import com.goodspartner.dto.RouteDto;
import com.goodspartner.entity.RoutePoint;
import com.goodspartner.service.DeliveryService;
import com.goodspartner.service.OrderExternalService;
import com.goodspartner.service.RouteService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/v1/deliveries")
public class DeliveryController {

    private final DeliveryService deliveryService;
    private final RouteService routeService;
    private final OrderExternalService orderExternalService;

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
    public DeliveryDto add(@ApiParam(value = "DeliveryDto that you want to add", type = "DeliveryDto", required = true)
                           @RequestBody DeliveryDto deliveryDto) {
        return deliveryService.add(deliveryDto);
    }

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
     * Delivery Order manipulation
     */

    @PreAuthorize("hasAnyRole('ADMIN', 'LOGIST')")
    @PostMapping("/{id}/orders")
    @ApiOperation(value = "Save valid orders with reference to delivery and fill known addresses cache",
            notes = "Provide an id to save orders")
    public void saveDeliveryOrders(@ApiParam(value = "delivery ID for orders to be referenced to", required = true)
                                   @PathVariable("id") UUID id,
                                   @RequestBody List<OrderDto> orderDtos) {

        orderExternalService.saveValidOrdersAndEnrichKnownAddressesCache(id, orderDtos);
    }

    /**
     * Delivery Routes manipulation
     */

    @PreAuthorize("hasAnyRole('ADMIN', 'LOGIST')")
    @PostMapping("/{id}/calculate")
    @ApiOperation(value = "Calculate routes by Delivery ID",
            notes = "Return DeliveryDto",
            response = DeliveryDto.class)
    public DeliveryDto calculateRoutes(@PathVariable("id") UUID deliveryId) {
        return deliveryService.calculateDelivery(deliveryId);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'LOGIST', 'DRIVER')")
    @PutMapping("/{id}/routes/{routeId}")
    public void updateRoute(@PathVariable int routeId, @RequestBody RouteDto routeDto) {
        routeService.update(routeId, routeDto);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'LOGIST', 'DRIVER')")
    @PutMapping("/{id}/routes/{routeId}/route-points/{routePointId}")
    public void updateRoutePoint(@PathVariable int routeId, @PathVariable String routePointId, @RequestBody RoutePoint routePoint) {
        routeService.updatePoint(routeId, routePointId, routePoint);
    }
}