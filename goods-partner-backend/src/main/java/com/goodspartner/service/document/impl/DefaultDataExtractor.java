package com.goodspartner.service.document.impl;

import com.goodspartner.dto.InvoiceDto;
import com.goodspartner.entity.Car;
import com.goodspartner.entity.Route;
import com.goodspartner.entity.RoutePoint;
import com.goodspartner.service.document.DataExtractor;

import java.util.List;
import java.util.Objects;

public class DefaultDataExtractor implements DataExtractor {

    private final static int THE_FIRST_ELEMENT = 0;
    private final static String HYPHEN = "-";
    private final static String WHITE_SPACE = " ";

    private List<RoutePoint> routePoints;
    private List<InvoiceDto> invoiceDtos;

    @Override
    public void setRoutePoints(List<RoutePoint> routePoints) {
        this.routePoints = routePoints;
    }

    @Override
    public void setInvoiceDtos(List<InvoiceDto> invoiceDtos) {
        this.invoiceDtos = invoiceDtos;
    }

    @Override
    public String extractCarName() {
        Car car = getCar();
        return car.getName().replaceAll(WHITE_SPACE, HYPHEN);
    }

    @Override
    public String extractCarLicencePlate() {
        Car car = getCar();
        return car.getLicencePlate();
    }

    @Override
    public String extractOrderNumber() {
        InvoiceDto invoiceDto = getInvoiceDto();
        return invoiceDto.getOrderNumber();
    }

    @Override
    public String extractDeliveryDate() {
        InvoiceDto invoiceDto = getInvoiceDto();
        return invoiceDto.getShippingDate().toString();
    }

    private Car getCar() {
        Objects.requireNonNull(routePoints);
        RoutePoint routePoint = getRoutePoint();
        Route route = routePoint.getRoute();
        return route.getCar();
    }

    private RoutePoint getRoutePoint() {
        return routePoints.get(THE_FIRST_ELEMENT);
    }

    private InvoiceDto getInvoiceDto() {
        Objects.requireNonNull(invoiceDtos);
        return invoiceDtos.get(THE_FIRST_ELEMENT);
    }
}