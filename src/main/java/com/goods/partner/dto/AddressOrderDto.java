package com.goods.partner.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class AddressOrderDto {
    private int orderId;
    private int orderNumber;
    private double orderTotalWeight;
}
