package com.goodspartner.dto;

import com.goodspartner.entity.OrderAddressValidationStatus;
import lombok.*;

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
    private MapPoint mapPoint;

    //Enrichment
    private List<ProductDto> products;
    private double orderWeight;
    private boolean validAddress;
    private OrderAddressValidationStatus validationStatus;
}