package com.goodspartner.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum DeliveryHistoryTemplate {
    DELIVERY_CREATED("${roleTranslated} ${userName} створив(ла) доставку"),
    DELIVERY_READY("Доставка готова до калькуляції"),
    DELIVERY_CALCULATED("${roleTranslated} ${userName} розрахував(ла) доставку"),
    DELIVERY_APPROVED("${roleTranslated} ${userName} підтвердив(ла) доставку"),
    DELIVERY_COMPLETED("Доставка переведена в статус виконана"),
    ROUTE_POINT_STATUS("${roleTranslated} ${userName} змінив(ла) статус точки маршрута до авто ${carName} ${carLicensePlate}, клієнт ${clientName}, адреса ${clientAddress} на ${routePointStatus}"),
    ROUTE_STATUS("${roleTranslated} ${userName} змінив(ла) статус маршрута до авто ${carName} ${carLicensePlate} на ${routeStatus}"),
    ROUTE_STATUS_AUTO("Змінився статус маршрута до авто ${carName} ${carLicensePlate} на ${routeStatus}"),
    ROUTE_START("${roleTranslated} ${userName} розпочав(ла) маршрут до авто ${carName} ${carLicensePlate}"),
    ORDERS_LOADING("${roleTranslated} ${userName} вивантажує замовлення замовлення з 1С"),
    ORDERS_LOADED("${roleTranslated} ${userName} вивантажив(ла) замовлення з 1С"),
    DRIVER_CLIENT_ADDRESS_UPDATE("${roleTranslated} ${userName} уточнив координати адресси клієнта: ${clientName}, адреса: ${clientAddress}");


    @Getter
    private final String template;
}
