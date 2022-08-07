package com.goodspartner.mapper;

import com.goodspartner.dto.RoutePointDto;
import com.goodspartner.entity.Address;
import com.goodspartner.entity.Order;
import com.goodspartner.entity.OrderedProduct;
import com.google.common.annotations.VisibleForTesting;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class RoutePointMapper {

    public List<RoutePointDto> mapOrders(List<Order> orders) {
        Map<Address, List<Order>> addressOrderMap = orders.stream()
                .collect(Collectors.groupingBy(Order::getAddress));

        List<RoutePointDto> routePointDtoList = new ArrayList<>(1);
        addressOrderMap.forEach((address, orderList) -> {
            List<RoutePointDto.AddressOrderDto> addressOrderDtos = mapOrdersAddress(orderList);
            double addRessTotalWeight = addressOrderDtos.stream()
                    .map(RoutePointDto.AddressOrderDto::getOrderTotalWeight)
                    .collect(Collectors.summarizingDouble(amount -> amount)).getSum();

            RoutePointDto routePointDto = new RoutePointDto();
            routePointDto.setAddress(address.getAddress());
            routePointDto.setClientId(address.getClient().getId());
            routePointDto.setClientName(address.getClient().getName());
            routePointDto.setOrders(addressOrderDtos);
            routePointDto.setAddressTotalWeight(addRessTotalWeight);
            routePointDtoList.add(routePointDto);
        });
        return routePointDtoList;
    }

    @VisibleForTesting
    List<RoutePointDto.AddressOrderDto> mapOrdersAddress(List<Order> orders) {
        return orders.stream()
                .map(this::mapOrderAddress)
                .collect(Collectors.toList());
    }

    @VisibleForTesting
    RoutePointDto.AddressOrderDto mapOrderAddress(Order order) {
        List<OrderedProduct> orderedProducts = order.getOrderedProducts();
        double orderTotalWeight = getOrderTotalWeight(orderedProducts);

        RoutePointDto.AddressOrderDto addressOrderDto = new RoutePointDto.AddressOrderDto();
        addressOrderDto.setId(order.getId());
        addressOrderDto.setOrderNumber(String.valueOf(order.getNumber()));
        addressOrderDto.setOrderTotalWeight(orderTotalWeight);

        return addressOrderDto;
    }

    // TODO: I think this is not mapper class, but class for calculations
    @VisibleForTesting
    double getOrderTotalWeight(List<OrderedProduct> orderedProducts) {
        return BigDecimal.valueOf(orderedProducts.stream()
                        .map(orderedProduct -> orderedProduct.getCount() * orderedProduct.getProduct().getKg())
                        .collect(Collectors.summarizingDouble(amount -> amount)).getSum())
                .setScale(2, RoundingMode.HALF_UP).doubleValue();
    }
}
