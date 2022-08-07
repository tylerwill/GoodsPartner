package com.goodspartner.dto;


import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
public class OrderDto {

    private int orderId;
    private String orderNumber;
    private LocalDate createdDate;
    private String clientName;
    private String address;
    private String managerFullName;
    private List<ProductDto> products;
    private double orderWeight;
}
