package com.goodspartner.dto;

import lombok.*;

import java.util.Objects;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Product {
    private String productName;
    private int amount;
    private String storeName;
    private double unitWeight;
    private double totalProductWeight;
    private double coefficient;
    private String measure;
    private ProductMeasureDetails productUnit;
    private ProductMeasureDetails productPackaging;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Product product = (Product) o;
        return amount == product.amount && Double.compare(product.unitWeight, unitWeight) == 0
                && Double.compare(product.totalProductWeight, totalProductWeight) == 0
                && Double.compare(product.coefficient, coefficient) == 0
                && productName.equals(product.productName)
                && Objects.equals(storeName, product.storeName)
                && Objects.equals(measure, product.measure)
                && Objects.equals(productUnit, product.productUnit)
                && Objects.equals(productPackaging, product.productPackaging);
    }

    @Override
    public int hashCode() {
        return Objects.hash(productName, amount, storeName, unitWeight, totalProductWeight, coefficient, measure, productUnit, productPackaging);
    }
}
