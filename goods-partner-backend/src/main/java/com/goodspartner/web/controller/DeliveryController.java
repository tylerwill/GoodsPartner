package com.goodspartner.web.controller;

import com.goodspartner.web.action.DeliveryAction;
import com.goodspartner.dto.CarDeliveryDto;
import com.goodspartner.dto.DeliveryDto;
import com.goodspartner.facade.DeliveryFacade;
import com.goodspartner.service.DeliveryService;
import com.goodspartner.web.controller.response.DeliveryActionResponse;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
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

    private final DeliveryFacade deliveryFacade;

    private final DeliveryService deliveryService;

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'LOGISTICIAN')")
    @ResponseStatus(HttpStatus.ACCEPTED)
    @ApiOperation(value = "Create new  Delivery")
    public DeliveryDto createDelivery(
            @ApiParam(value = "DeliveryDto that you want to create", type = "DeliveryResponse", required = true)
            @RequestBody DeliveryDto delivery) {
        return deliveryFacade.add(delivery);
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'LOGISTICIAN', 'DRIVER')")
    @ApiOperation(value = "Get all Deliveries", notes = "Return list of DeliveryResponse", response = List.class)
    public List<DeliveryDto> findAll() {
        return deliveryService.findAll();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'LOGISTICIAN', 'DRIVER')")
    @ApiOperation(value = "Find Delivery by id",
            notes = "Provide an id to look up specific delivery",
            response = DeliveryDto.class)
    public DeliveryDto findById(
            @ApiParam(value = "ID value for the delivery you need to retrieve", required = true)
            @PathVariable UUID id) {
        return deliveryService.findById(id);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'LOGISTICIAN')")
    @ApiOperation(value = "Remove Delivery by ID", notes = "Provide an ID to remove up specific delivery")
    public DeliveryDto delete(@ApiParam(value = "ID value for the delivery you need to retrieve", required = true)
                              @PathVariable UUID id) {
        return deliveryService.delete(id);
    }

    // TODO rework on acton
    @PostMapping("/{id}/calculate")
    @PreAuthorize("hasAnyRole('ADMIN', 'LOGISTICIAN')")
    @ResponseStatus(HttpStatus.ACCEPTED)
    @ApiOperation(value = "Calculate routes by delivery ID",
            notes = "Return DeliveryResponse",
            response = DeliveryDto.class)
    public DeliveryDto calculateDelivery(@ApiParam(value = "ID of Delivery to be calculated", required = true)
                                         @PathVariable UUID id) {
        return deliveryService.calculateDelivery(id);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'LOGISTICIAN')")
    @PostMapping("/{id}/{action}")
    @ApiOperation(value = "Approve Delivery",
            notes = "Provide an id to approve delivery")
    public DeliveryActionResponse approve(@ApiParam(value = "ID of Delivery to be approved", required = true)
                                          @PathVariable UUID id,
                                          @PathVariable String action) {
        return deliveryService.approve(id, DeliveryAction.of(action));
    }

    // DriverRelated TODO think about /driver/deliveries

    @PreAuthorize("hasAnyRole('DRIVER')")
    @GetMapping("/by-driver")
    @ApiOperation(value = "Get all Deliveries by authorisation",
            notes = "Return list of Deliveries by respective user",
            response = List.class)
    public List<DeliveryDto> getAllDriverRelated(OAuth2AuthenticationToken authentication) {
        return deliveryService.findAll(authentication);
    }

    // TODO do we really need this endpoint since we would get carloads by separate endpoint
    @PreAuthorize("hasAnyRole('DRIVER')")
    @GetMapping("/{id}/by-driver")
    @ApiOperation(value = "Find Delivery by id for authenticated driver",
            notes = "Provide an id to look up specific delivery",
            response = CarDeliveryDto.class)
    public CarDeliveryDto getByCarDeliveryById(@ApiParam(value = "ID value for the delivery you need to retrieve", required = true)
                                               @PathVariable("id") UUID id,
                                               OAuth2AuthenticationToken authentication) {
        return deliveryService.findById(id, authentication);
    }
}