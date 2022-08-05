package com.goodspartner.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class OrderData {

    private String clientName;
    private String address;
    private String managerFullName;
    private List<ProductDto> products;
    private double orderWeight;

}
