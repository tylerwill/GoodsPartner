package com.goodspartner.web.controller;

import com.goodspartner.action.OrderAction;
import com.goodspartner.dto.OrderDto;
import com.goodspartner.service.OrderExternalService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
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
    @PostMapping("/{id}/{action}/{deliveryDate}")
    @ApiOperation(value = "Update order delivery date",
            notes = "Return updated order",
            response = OrderDto.class
    )
    public OrderDto updateDeliveryDate(
            @ApiParam(value = "ID value for the order you need to update", required = true)
            @PathVariable int id,
            @ApiParam(value = "New delivery date value", required = true)
            @PathVariable String deliveryDate,
            @PathVariable String action) {
        return orderExternalService.updateDeliveryDate(id, LocalDate.parse(deliveryDate), OrderAction.of(action));
    }

}