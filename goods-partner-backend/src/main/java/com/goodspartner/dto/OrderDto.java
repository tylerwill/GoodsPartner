package com.goodspartner.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderDto {

    private int id;
    @JsonIgnore
    private UUID deliveryID;
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