package com.goodspartner.dto;


import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OrderInfoDto {
    private int orderId;
    private String orderNumber;
    private List<ProductInfoDto> products;
}
