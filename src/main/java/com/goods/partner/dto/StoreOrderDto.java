package com.goods.partner.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class StoreOrderDto {

    private int orderId;
    private String orderNumber;
    private double totalOrderWeight;

}
