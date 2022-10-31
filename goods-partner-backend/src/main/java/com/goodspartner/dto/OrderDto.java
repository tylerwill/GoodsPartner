package com.goodspartner.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.goodspartner.entity.DeliveryType;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
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
    private DeliveryType deliveryType;
    private boolean excluded;
    private boolean dropped;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate deliveryDate;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm")
    private LocalTime deliveryStart;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm")
    private LocalTime deliveryFinish;

    // Address
    private String clientName;
    private String address;
    private MapPoint mapPoint;

    //Enrichment
    private List<Product> products;
    private double orderWeight;
}