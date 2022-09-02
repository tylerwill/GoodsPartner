package com.goodspartner.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderDto {

    // TODO add refKey property
    private int id;
    private String refKey;
    private String orderNumber;
    private LocalDate createdDate;
    private String clientName;
    private String address;
    private String comment;
    private String managerFullName;

    //Enrichment
    private List<ProductDto> products;
    private double orderWeight;
    private boolean validAddress;
}