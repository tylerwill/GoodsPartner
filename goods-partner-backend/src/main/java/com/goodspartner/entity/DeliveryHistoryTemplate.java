package com.goodspartner.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum DeliveryHistoryTemplate {
    DELIVERY_CREATED("{role} {userEmail} створив(ла) доставку"),
    DELIVERY_UPDATED("{role} {userEmail} оновив(ла) доставку"),
    DELIVERY_APPROVED("{role} {userEmail} підтвердив(ла) доставку"),
    DELIVERY_CALCULATED("{role} {userEmail} розрахував(ла) доставку"),
    DELIVERY_COMPLETED("Доставка переведена в статус виконана"),
    ROUTE_POINT_STATUS("${role} ${userEmail} змінив(ла) статус точки маршрута до авто ${carName} ${carLicensePlate}, клієнт ${clientName}, адреса ${clientAddress} на ${routePointStatus}"),
    ROUTE_STATUS("${role} ${userEmail} змінив(ла) статус маршрута до авто ${carName} ${carLicensePlate} на ${routeStatus}"),
    ROUTE_STATUS_AUTO("Змінився статус маршрута до авто ${carName} ${carLicensePlate} на ${routeStatus}"),
    ROUTE_START("${role} ${userEmail} розпочав(ла) маршрут до авто ${carName} ${carLicensePlate}");

    @Getter
    private final String template;
}
