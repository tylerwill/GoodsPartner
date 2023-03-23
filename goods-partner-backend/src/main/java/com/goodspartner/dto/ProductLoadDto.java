package com.goodspartner.dto;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class ProductLoadDto {
    private String orderNumber;
    private String car;
    private int amount;
    private double weight;
    private double totalWeight;
    private ProductMeasureDetails productUnit;
    private ProductMeasureDetails productPackaging;
}
