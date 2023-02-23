package com.goodspartner.dto;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InvoiceProduct {
    private String productName;
    private String lineNumber;
    private String totalProductWeight;
    private String coefficient;
    private String measure;
    private String priceAmount;
    private String amountWithoutPDV;
    private String priceWithoutPDV;
    private String price;
    private String uktzedCode;
    private Double priceAmountPDV;
    private String qualityUrl;
    private int amount;
    private ProductMeasureDetails productUnit;
    private ProductMeasureDetails productPackaging;
}
