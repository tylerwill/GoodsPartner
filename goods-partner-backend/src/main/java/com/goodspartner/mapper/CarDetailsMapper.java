package com.goodspartner.mapper;

import com.goodspartner.dto.AddressOrderDto;
import com.goodspartner.dto.CarLoadDto;
import com.goodspartner.dto.OrderInfoDto;
import com.goodspartner.dto.ProductInfoDto;
import com.goodspartner.dto.RouteDto;
import com.goodspartner.dto.RoutePointDto;
import com.goodspartner.entity.Order;
import com.goodspartner.entity.OrderedProduct;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class CarDetailsMapper {

    public List<CarLoadDto> map(List<RouteDto> routes, List<Order> orders) {
        return routes.stream().map(route -> routeToCarDetails(route, orders)).toList();
    }

    private CarLoadDto routeToCarDetails(RouteDto route, List<Order> orders) {
        List<RoutePointDto> routePoints = route.getRoutePoints();
        List<OrderInfoDto> ordersInfo = routePoints.stream().map(routePoint -> {
                    List<AddressOrderDto> addressOrderDtos = routePoint.getOrders();
                    List<Integer> ordersId = addressOrderDtos.stream().map(AddressOrderDto::getOrderId).toList();
                    return orders.stream().filter(order -> ordersId.contains(order.getId())).toList();
                }).flatMap(List::stream).map(this::mapOrder)
                .collect(Collectors.toList());

        Collections.reverse(ordersInfo);
        return CarLoadDto.builder()
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
