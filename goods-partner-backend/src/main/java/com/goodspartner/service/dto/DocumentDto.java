package com.goodspartner.service.dto;

import lombok.Getter;
import lombok.Setter;

import java.io.OutputStream;

@Setter
@Getter
public class DocumentDto {
    private String carName;
    private String carLicencePlate;
    private String orderNumber;
    private String deliveryDate;
    private OutputStream documentContent;
}