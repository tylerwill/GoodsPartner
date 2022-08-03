package com.goods.partner.dto;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProductInfoDto {
    private String productName;
    private int amount;
}
