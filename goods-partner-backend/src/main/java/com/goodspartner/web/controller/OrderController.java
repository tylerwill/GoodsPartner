package com.goodspartner.web.controller;

import com.goodspartner.action.OrderAction;
import com.goodspartner.dto.OrderDto;
import com.goodspartner.dto.UpdateDto;
import com.goodspartner.service.OrderExternalService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/v1/orders", produces = MediaType.APPLICATION_JSON_VALUE)
public class OrderController {
    private final OrderExternalService orderExternalService;

    @PreAuthorize("hasAnyRole('ADMIN', 'LOGIST')")
    @GetMapping()
    @ApiOperation(value = "Get orders",
            notes = "Return orders filtered by excluded/dropped attribute",
            response = Page.class
    )
    public List<OrderDto> getFilteredOrdersPage(
            @RequestParam(value = "excluded", defaultValue = "false") boolean excluded,
            @RequestParam(value = "dropped", defaultValue = "false") boolean dropped) {
        return orderExternalService.getFilteredOrders(excluded, dropped);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'LOGIST')")
    @PostMapping("/{action}")
    @ApiOperation(value = "Update orders delivery date",
            notes = "Return updated orders",
            response = OrderDto.class,
            responseContainer = "List"
    )
    public List<OrderDto> updateDeliveryDate(
            @ApiParam(value = "UpdateDto with new date and orders id", type = "UpdateDto", required = true)
            @RequestBody UpdateDto updateDto,
            @PathVariable String action) {
        return orderExternalService.updateDeliveryDate(updateDto, OrderAction.of(action));
    }

}

