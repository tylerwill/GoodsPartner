package com.goods.partner.mapper;

import com.goods.partner.dto.*;
import com.goods.partner.entity.Order;
import com.goods.partner.entity.OrderedProduct;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class CarDetailsMapper {

    public List<CarLoadDetailsDto> map(List<RouteDto> routes, List<Order> orders) {
        return routes.stream().map(route -> routeToCarDetails(route, orders)).toList();
    }

    private CarLoadDetailsDto routeToCarDetails(RouteDto route, List<Order> orders) {
        List<RoutePointDto> routePoints = route.getRoutePoints();
        List<OrderInfoDto> ordersInfo = routePoints.stream().map(routePoint -> {
                    List<AddressOrderDto> addressOrderDtos = routePoint.getOrders();
                    List<Integer> ordersId = addressOrderDtos.stream().map(AddressOrderDto::getOrderId).toList();
                    return orders.stream().filter(order -> ordersId.contains(order.getId())).toList();
                }).flatMap(List::stream).map(this::mapOrder)
                .collect(Collectors.toList());

        Collections.reverse(ordersInfo);
        return CarLoadDetailsDto.builder()
                .car(route.getCar())
                .orders(ordersInfo)
                .build();
    }

    private OrderInfoDto mapOrder(Order order) {
        return OrderInfoDto.builder()
                .orderId(order.getId())
                .orderNumber(String.valueOf(order.getNumber()))
                .products(mapOrderToProductInfo(order))
                .build();
    }

    private List<ProductInfoDto> mapOrderToProductInfo(Order order) {
        return order.getOrderedProducts().stream()
                .map(this::mapOrderedProductToProductInfo).toList();
    }

    private ProductInfoDto mapOrderedProductToProductInfo(OrderedProduct orderedProduct) {
        return ProductInfoDto.builder()
                .productName(orderedProduct.getProduct().getName())
                .amount(orderedProduct.getCount())
                .weight(getProductTotalWeight(orderedProduct))
                .build();
    }

    private double getProductTotalWeight(OrderedProduct orderedProduct) {
        return BigDecimal.valueOf(orderedProduct.getCount() * orderedProduct.getProduct().getKg())
                .setScale(2, RoundingMode.HALF_UP).doubleValue();
    }
}
