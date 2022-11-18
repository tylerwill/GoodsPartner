package com.goodspartner.web.controller;

import com.goodspartner.dto.OrderDto;
import com.goodspartner.web.controller.request.RemoveOrdersRequest;
import com.goodspartner.web.controller.request.RescheduleOrdersRequest;
import com.goodspartner.service.OrderExternalService;
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
@RequestMapping(value = "/api/v1/orders", produces = MediaType.APPLICATION_JSON_VALUE)
public class OrderController {
    private final OrderExternalService orderExternalService;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'LOGISTICIAN', 'DRIVER')")
    @ApiOperation(value = "Find Orders by delivery ID",
            notes = "Provide an delivery ID to look up related orders",
            response = List.class)
    public List<OrderDto> findByDeliveryId(@RequestParam("deliveryId") UUID deliveryId){
        return orderExternalService.findByDeliveryId(deliveryId);
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
        return orderExternalService.update(id, orderDto);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'LOGIST')")
    @GetMapping("/skipped")
    @ApiOperation(value = "Get skipped orders",
            notes = "Return orders filtered by excluded/dropped attribute",
            response = OrderDto.class,
            responseContainer = "List"
    )
    public List<OrderDto> getSkippedOrders() {
        return orderExternalService.getSkippedOrders();
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'LOGIST')")
    @GetMapping("/completed")
    @ApiOperation(value = "Get completed orders",
            notes = "Return orders filtered by excluded/dropped attribute",
            response = OrderDto.class,
            responseContainer = "List"
    )
    public List<OrderDto> getCompletedOrders() {
        return orderExternalService.getCompletedOrders();
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'LOGIST')")
    @GetMapping("/scheduled")
    @ApiOperation(value = "Get completed orders",
            notes = "Return orders filtered by excluded/dropped attribute",
            response = OrderDto.class,
            responseContainer = "List"
    )
    public List<OrderDto> getScheduledOrders() {
        return orderExternalService.getScheduledOrders();
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
        return orderExternalService.rescheduleSkippedOrders(rescheduleOrdersRequest);
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
        return orderExternalService.removeExcludedOrders(removeOrdersRequest);
    }

}

