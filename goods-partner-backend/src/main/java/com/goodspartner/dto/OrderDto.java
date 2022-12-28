package com.goodspartner.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.goodspartner.entity.DeliveryType;
import com.goodspartner.web.localization.DeliveryTypeDeserializer;
import com.goodspartner.web.localization.DeliveryTypeSerializer;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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

    private Integer id; // Keep boxed to differentiate between new DTO and specific one

    @ApiModelProperty(notes = "The unique uuid of order from 1C")
    private String refKey;
    private String orderNumber;
    private String comment;
    private String managerFullName;
    private String excludeReason;

    private UUID deliveryId;

    @JsonSerialize(using = DeliveryTypeSerializer.class)
    @JsonDeserialize(using = DeliveryTypeDeserializer.class)
    private DeliveryType deliveryType;

    private boolean frozen;
    private boolean excluded;
    private boolean dropped;

    private LocalDate shippingDate;
    private LocalDate rescheduleDate;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm")
    private LocalTime deliveryStart;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm")
    private LocalTime deliveryFinish;

    // Address
    private String clientName;
    private String address;
    private MapPoint mapPoint;

    // Enrichment
    private List<Product> products;
    private double orderWeight;
}