package com.goodspartner.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AddressOrderDto {
    private int orderId;
    private String orderNumber;
    private double orderTotalWeight;
}