package com.goodspartner.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalTime;
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
    private UUID deliveryId;
    @ApiModelProperty(notes = "The unique uuid of order from 1C")
    private String refKey;

    private String orderNumber;
    private LocalDate createdDate;
    private String comment;
    private String managerFullName;
    private boolean isFrozen;
    private LocalTime deliveryStart;
    private LocalTime deliveryFinish;

    // Address
    private String clientName;
    private String address;
    private MapPoint mapPoint;

    //Enrichment
    private List<Product> products;
    private double orderWeight;
}