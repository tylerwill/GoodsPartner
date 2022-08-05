package com.goodspartner.dto;


import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class OrderDto {

    private int orderId;
    private String orderNumber;
    private LocalDate createdDate;
    private OrderData orderData;
}
