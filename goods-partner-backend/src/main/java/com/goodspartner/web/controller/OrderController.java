package com.goodspartner.web.controller;

import com.goodspartner.dto.OrderDto;
import com.goodspartner.mapper.OrderExternalMapper;
import com.goodspartner.service.OrderExternalService;
import com.goodspartner.web.controller.request.RemoveOrdersRequest;
import com.goodspartner.web.controller.request.RescheduleOrdersRequest;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/v1/orders", produces = MediaType.APPLICATION_JSON_VALUE)
public class OrderController {

    private final OrderExternalService orderExternalService;

    private final OrderExternalMapper orderExternalMapper;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'LOGISTICIAN', 'DRIVER')")
    @ApiOperation(value = "Find Orders by delivery ID",
            notes = "Provide an delivery ID to look up related orders",
            response = List.class)
    public List<OrderDto> findByDeliveryId(@RequestParam("deliveryId") UUID deliveryId,
                                           OAuth2AuthenticationToken authentication) {
        return orderExternalService.findByDeliveryId(deliveryId, authentication)
                .stream()
                .map(orderExternalMapper::mapToDto)
                .collect(Collectors.toList());
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'LOGIST')")
    @PutMapping("/{id}")
    @ApiOperation(value = "Update order",
            notes = "Return updated order",
            response = OrderDto.class,
            responseContainer = "List"
    )
    public OrderDto update(@PathVariable int id,
                           @RequestBody OrderDto orderDto) {
        return orderExternalMapper.mapToDto(orderExternalService.update(id, orderDto));
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'LOGIST')")
    @GetMapping("/skipped")
    @ApiOperation(value = "Get skipped orders",
            notes = "Return orders filtered by excluded/dropped attribute",
            response = OrderDto.class,
            responseContainer = "List"
    )
    public List<OrderDto> getSkippedOrders() {
        return orderExternalService.getSkippedOrders()
                .stream()
                .map(orderExternalMapper::mapToDto)
                .toList();
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'LOGIST')")
    @GetMapping("/completed")
    @ApiOperation(value = "Get completed orders",
            notes = "Return orders filtered by excluded/dropped attribute",
            response = OrderDto.class,
            responseContainer = "List"
    )
    public List<OrderDto> getCompletedOrders() {
        return orderExternalService.getCompletedOrders()
                .stream()
                .map(orderExternalMapper::mapToDto)
                .toList();
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'LOGIST')")
    @GetMapping("/scheduled")
    @ApiOperation(value = "Get completed orders",
            notes = "Return orders filtered by excluded/dropped attribute",
            response = OrderDto.class,
            responseContainer = "List"
    )
    public List<OrderDto> getScheduledOrders() {
        return orderExternalService.getScheduledOrders()
                .stream()
                .map(orderExternalMapper::mapToDto)
                .toList();
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'LOGIST')")
    @PostMapping("/reschedule") // TODO /skipped/reschedule
    @ApiOperation(value = "Update orders delivery date",
            notes = "Return updated orders",
            response = OrderDto.class,
            responseContainer = "List"
    )
    public List<OrderDto> rescheduleOrders(
            @ApiParam(value = "UpdateDto with new date and orders id", type = "UpdateDto", required = true)
            @RequestBody RescheduleOrdersRequest rescheduleOrdersRequest) {
        return orderExternalService.rescheduleSkippedOrders(rescheduleOrdersRequest)
                .stream()
                .map(orderExternalMapper::mapToDto)
                .toList();
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'LOGIST')")
    @PostMapping("/remove") // TODO /skipped/remove
    @ApiOperation(value = "Remove orders from excluded state",
            notes = "Return removed orders",
            response = OrderDto.class,
            responseContainer = "List"
    )
    public List<OrderDto> removeOrders(
            @ApiParam(value = "Remove chosen order ids", type = "UpdateDto", required = true)
            @RequestBody RemoveOrdersRequest removeOrdersRequest) {
        return orderExternalService.removeExcludedOrders(removeOrdersRequest).stream()
                .map(orderExternalMapper::mapToDto)
                .toList();

    }

}

