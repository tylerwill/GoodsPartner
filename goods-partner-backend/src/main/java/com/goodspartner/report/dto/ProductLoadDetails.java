package com.goodspartner.report.dto;

import com.goodspartner.dto.Product;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@AllArgsConstructor
@Builder
@Data
public class ProductLoadDetails {
    private Product product;
    private String clientName;
    private String clientAddress;
    private String orderNumber;
    private String comment;
}
