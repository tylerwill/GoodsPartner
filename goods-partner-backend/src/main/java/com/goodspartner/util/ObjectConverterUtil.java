package com.goodspartner.util;

import com.goodspartner.entity.OrderExternal;
import com.goodspartner.entity.RoutePoint;
import lombok.experimental.UtilityClass;

import java.util.List;
import java.util.stream.Collectors;

@UtilityClass
public class ObjectConverterUtil {

    public static List<String> getOrdersRefKeysFromRoutePointList(List<RoutePoint> routePoints) {
        return routePoints.stream()
                .flatMap(routePoint -> routePoint.getOrders().stream())
                .map(OrderExternal::getRefKey)
                .collect(Collectors.toList());
    }

    public static List<String> getOrdersRefKeysFromRoutePoint(RoutePoint routePoint) {
        return getOrdersRefKeysFromRoutePointList(List.of(routePoint));
    }
}
