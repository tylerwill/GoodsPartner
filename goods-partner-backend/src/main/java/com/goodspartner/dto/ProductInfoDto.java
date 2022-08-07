package com.goodspartner.dto;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProductInfoDto {
    private String productName;
    private int amount;
    private double weight;
}
