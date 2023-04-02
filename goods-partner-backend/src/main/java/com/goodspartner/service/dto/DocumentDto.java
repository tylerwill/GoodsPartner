package com.goodspartner.service.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class DocumentDto {
    private String carName;
    private String carLicencePlate;
    private String orderNumber;
    private String deliveryDate;
    private String documentContent;
}