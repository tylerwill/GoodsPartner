package com.goodspartner.dto;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class ProductShippingDto {
    private String article;
    private int totalAmount;
    private double totalWeight;
    private List<ProductLoadDto> productLoadDtos;
}
