package com.goods.partner.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProductDto {

    private String productName;
    private int amount;
    private String storeName;
    private double unitWeight;
    private double totalProductWeight;

}
