package com.goodspartner.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InvoiceDto {
    private String refKey;
    private Boolean deletionMark;
    private String number;
    private String documentDate;
    private Boolean posted;
    private Double invoiceAmount;
    private String deliveryAddress;

    private List<InvoiceProduct> products;

    private String companyName;
    private String companyAccount;
    private String clientName;
    private String companyInformation;
    private String storeAddress;
    private String clientContract;
    private String orderInfo;
    private Double invoiceAmountPDV;
    private Double invoiceAmountWithoutPDV;
    private String managerFullName;
    private String textNumeric;

    private String orderNumber;
    private String orderDate;
    private String bankName;
    private String edrpouCode;
    private String mfoCode;
}
