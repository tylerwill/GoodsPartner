package com.goodspartner.dto;

import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
// common
public class OrderDto {

    private int id;
    private String orderNumber;
    private List<ProductDto> products;

    //Enrichment
    private LocalDate createdDate;
    private String clientName;
    private String address;
    private String managerFullName;
    private double orderWeight;

}
