package com.goodspartner.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InvoiceProduct {
    private String productName;
    private String lineNumber;
    private Double totalProductWeight;
    private Double coefficient;
    private String measure;
    private String uktzedCode;
    private int amount;
    private String amountWithoutPDV;
    private String price;
    private String priceAmount;
    private String priceWithoutPDV;
    private Double priceAmountPDV;
    private String qualityUrl;
    private ProductMeasureDetails productUnit;
    private ProductMeasureDetails productPackaging;
}
