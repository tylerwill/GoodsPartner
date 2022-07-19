package com.goods.partner.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AddressOrderDto {
    private int orderId;
    private int orderNumber;
    private double orderTotalWeight;
}