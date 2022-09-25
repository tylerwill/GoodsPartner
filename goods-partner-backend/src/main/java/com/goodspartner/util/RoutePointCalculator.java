package com.goodspartner.util;

import com.goodspartner.dto.MapPoint;
import com.goodspartner.dto.Product;
import com.goodspartner.entity.AddressExternal;
import com.goodspartner.entity.AddressExternal.OrderAddressId;
import com.goodspartner.entity.OrderExternal;
import com.goodspartner.entity.RoutePoint;
import com.goodspartner.entity.RoutePointStatus;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class RoutePointCalculator {

    public List<RoutePoint> mapOrders(List<OrderExternal> orders) {
        List<RoutePoint> routePointList = new ArrayList<>();

        Map<AddressExternal, List<OrderExternal>> addressOrderMap = orders
                .stream()
                .collect(Collectors.groupingBy(
                        OrderExternal::getAddressExternal,
                        LinkedHashMap::new,
                        Collectors.toList()));

        addressOrderMap.forEach((addressExternal, orderList) -> {
            List<RoutePoint.AddressOrder> addressOrders = mapOrdersAddress(orderList);

            double addressTotalWeight = addressOrders.stream()
                    .map(RoutePoint.AddressOrder::getOrderTotalWeight)
                    .collect(Collectors.summarizingDouble(amount -> amount)).getSum();

            OrderAddressId orderAddressId = addressExternal.getOrderAddressId();

            RoutePoint routePoint = new RoutePoint();
            routePoint.setId(UUID.randomUUID());
            routePoint.setStatus(RoutePointStatus.PENDING);
            routePoint.setAddress(orderAddressId.getOrderAddress());
            routePoint.setClientName(orderAddressId.getClientName());
            routePoint.setOrders(addressOrders);
            routePoint.setAddressTotalWeight(addressTotalWeight);
            routePoint.setMapPoint(getMapPoint(addressExternal));
            routePointList.add(routePoint);
        });
        return routePointList;
    }

    private MapPoint getMapPoint(AddressExternal addressExternal) {
        return MapPoint.builder()
                .status(MapPoint.AddressStatus.KNOWN)
                .address(addressExternal.getValidAddress())
                .longitude(addressExternal.getLongitude())
                .latitude(addressExternal.getLatitude())
                .build();
    }

    private List<RoutePoint.AddressOrder> mapOrdersAddress(List<OrderExternal> orders) {
        return orders.stream()
                .map(this::mapOrderAddress)
                .collect(Collectors.toList());
    }

    private RoutePoint.AddressOrder mapOrderAddress(OrderExternal order) {
        List<Product> products = order.getProducts();
        double orderTotalWeight = getOrderTotalWeight(products);

        RoutePoint.AddressOrder addressOrder = new RoutePoint.AddressOrder();
        addressOrder.setId(order.getId());
        addressOrder.setOrderNumber(String.valueOf(order.getOrderNumber()));
        addressOrder.setComment(order.getComment());
        addressOrder.setOrderTotalWeight(orderTotalWeight);

        return addressOrder;
    }

    private double getOrderTotalWeight(List<Product> orderedProducts) {
        return BigDecimal.valueOf(orderedProducts.stream()
                .map(Product::getTotalProductWeight)
                .collect(Collectors.summarizingDouble(weight -> weight)).getSum())
                .setScale(2, RoundingMode.HALF_UP).doubleValue();
    }
}
