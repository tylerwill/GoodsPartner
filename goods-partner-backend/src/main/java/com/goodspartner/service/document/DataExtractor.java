package com.goodspartner.service.document;

import com.goodspartner.dto.InvoiceDto;
import com.goodspartner.entity.RoutePoint;

import java.util.List;

public interface DataExtractor {
    void setRoutePoints(List<RoutePoint> routePoints);
    void setInvoiceDtos(List<InvoiceDto> invoiceDtos);
    String extractCarName();
    String extractCarLicencePlate();
    String extractOrderNumber();
    String extractDeliveryDate();
}
