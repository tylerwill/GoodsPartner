package com.goods.partner.dto;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrderDto {

    private int orderId;
    private int orderNumber;
    private OrderData orderData;

}
