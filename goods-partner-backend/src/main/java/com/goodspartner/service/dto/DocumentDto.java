package com.goodspartner.service.dto;

import com.goodspartner.service.document.dto.RouteSheet;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
public class DocumentDto {
    private String carName;
    private String carLicencePlate;
    private String driverName;
    private String orderNumber;
    private String deliveryDate;
    private String documentContent;
    private List<RouteSheet> routeSheets;
}