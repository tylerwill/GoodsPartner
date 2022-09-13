package com.goodspartner.dto;

import io.swagger.annotations.ApiModelProperty;
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
    @ApiModelProperty(notes = "The unique uuid of order from 1C")
    private String refKey;
    private String orderNumber;
    private LocalDate createdDate;
    private String comment;
    private String managerFullName;

    // Address
    private String clientName;
    private String address; // OrderAddress
    private MapPoint mapPoint;

    //Enrichment
    private List<ProductDto> products;
    private double orderWeight;
}