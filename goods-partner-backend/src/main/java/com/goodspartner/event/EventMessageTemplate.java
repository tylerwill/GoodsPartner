package com.goodspartner.event;

import com.goodspartner.entity.Route;
import com.goodspartner.entity.RoutePoint;
import com.goodspartner.util.AuditorBuilder;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.apache.commons.text.StringSubstitutor;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@AllArgsConstructor
public enum EventMessageTemplate {
    // Delivery
    DELIVERY_CREATED("${roleTranslated} ${userName} створив(ла) доставку"),
    DELIVERY_READY("Доставка готова до калькуляції"),
    DELIVERY_CALCULATED("${roleTranslated} ${userName} розрахував(ла) доставку"),
    DELIVERY_CALCULATION_FAILED("Помилка розрахування доставки"),
    DELIVERY_APPROVED("${roleTranslated} ${userName} підтвердив(ла) доставку"),
    DELIVERY_COMPLETED("Доставка переведена в статус виконана"),

    // Route
    ROUTE_STATUS("${roleTranslated} ${userName} змінив(ла) статус маршрута до авто ${carName} ${carLicensePlate} на ${routeStatus}"),
    ROUTE_STATUS_AUTO("Змінився статус маршрута до авто ${carName} ${carLicensePlate} на ${routeStatus}"),
    ROUTE_START("${roleTranslated} ${userName} розпочав(ла) маршрут до авто ${carName} ${carLicensePlate}"),
    ROUTES_UPDATED("Маршрути оновлені"),

    // Route point
    ROUTE_POINT_STATUS("${roleTranslated} ${userName} змінив(ла) статус точки маршрута до авто ${carName} ${carLicensePlate}, клієнт ${clientName}, адреса ${clientAddress} на ${routePointStatus}"),
    ROUTE_POINT_TIME_RANGE_WARNING("Увага! Маршрут ${carName} ${carLicensePlate} має зупинки що не потрапляють у заданий час доставки. При потребі перебудуйте маршрут."),

    // Orders
    ORDERS_LOADING("Розпочато синхронізацію замовлень з 1С для доставки на ${deliveryDate}"),
    ORDERS_LOADED("Збережено ${loadedOrders} замовлень з 1С"),
    ORDERS_LOADING_FAILED("Помилка під час вивантаження замовлень з 1С"),
    DROPPED_ORDERS("Увага! У розрахунок доставки не увійшли ${droppedOrdersAmount} замовлень"),
    ORDERS_UPDATED("Замовлення оновленні"),

    // Address
    DRIVER_CLIENT_ADDRESS_UPDATE("${roleTranslated} ${userName} уточнив координати адресси клієнта: ${clientName}, адреса: ${clientAddress}"),

    ;

    @Getter
    private final String template;

    public String withValuesAndAudit(Map<EventPlaceholder, String> values) {
        Map<String, String> remapped = values
                .entrySet()
                .stream()
                .collect(Collectors.toMap(entry -> entry.getKey().getValue(), Map.Entry::getValue));

        remapped.putAll(AuditorBuilder.getCurrentAuditorData());

        return StringSubstitutor.replace(getTemplate(), remapped);
    }

    public String withValues(EventPlaceholder eventPlaceholder, String value) {
        return StringSubstitutor.replace(getTemplate(), Map.of(eventPlaceholder.getValue(), value));
    }

    public String withAudit() {
        Map<String, String> currentAuditorData = AuditorBuilder.getCurrentAuditorData();
        return StringSubstitutor.replace(getTemplate(), currentAuditorData);
    }

    public String withRouteValues(Route route) {
        Map<EventPlaceholder, String> values = new HashMap<>();
        values.put(EventPlaceholder.CAR_NAME, route.getCar().getName());
        values.put(EventPlaceholder.CAR_LICENCE_PLATE, route.getCar().getLicencePlate());
        values.put(EventPlaceholder.ROUTE_STATUS, route.getStatus().getStatus());
        return withValuesAndAudit(values);
    }

    public String withRoutePointValues(RoutePoint routePoint) {
        Map<EventPlaceholder, String> values = new HashMap<>();

        values.put(EventPlaceholder.CLIENT_NAME, routePoint.getClientName());
        values.put(EventPlaceholder.CLIENT_ADDRESS, routePoint.getAddress());
        values.put(EventPlaceholder.ROUTE_POINT_STATUS, routePoint.getStatus().toString());

        Route route = routePoint.getRoute();
        values.put(EventPlaceholder.CAR_NAME, route.getCar().getName());
        values.put(EventPlaceholder.CAR_LICENCE_PLATE, route.getCar().getLicencePlate());
        values.put(EventPlaceholder.ROUTE_STATUS, route.getStatus().getStatus());

        return withValuesAndAudit(values);
    }

    @AllArgsConstructor
    public enum EventPlaceholder {
        LOADED_ORDERS_VALUE("loadedOrders"),
        DELIVERY_DATE("deliveryDate"),
        DROPPED_ORDERS_AMOUNT("droppedOrdersAmount"),

        // Route
        CAR_NAME("carName"),
        CAR_LICENCE_PLATE("carLicensePlate"),
        ROUTE_STATUS("routeStatus"),

        // RoutePoint
        CLIENT_NAME("clientName"),
        CLIENT_ADDRESS("clientAddress"),
        ROUTE_POINT_STATUS("routePointStatus")
        ;

        @Getter
        private final String value;
    }



}
