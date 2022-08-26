package com.goodspartner.dto;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductDto {
    private String productName;
    private int amount;
    private String storeName;
    private double unitWeight;
    private double totalProductWeight;
}
