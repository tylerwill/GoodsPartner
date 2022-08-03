package com.goods.partner.dto;


import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OrderInfoDto {
    private int orderId;
    private int orderNumber;
    private List<ProductInfoDto> products;
}
