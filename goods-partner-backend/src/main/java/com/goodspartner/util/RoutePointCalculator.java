package com.goodspartner.util;

import com.goodspartner.dto.MapPoint;
import com.goodspartner.dto.OrderDto;
import com.goodspartner.dto.ProductDto;
import com.goodspartner.dto.RoutePointDto;
import com.goodspartner.entity.RoutePointStatus;
import org.springframework.data.util.Pair;
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

    /**
     * TODO refactor Pair<Address, Client> by introducing separate AddressDto which will encapsulates the data
     * and will be included in OrderDto
     */
    public List<RoutePointDto> mapOrders(List<OrderDto> orders) {
        Map<Pair<MapPoint, String>, List<OrderDto>> addressOrderMap = orders.stream()
                .collect(Collectors.groupingBy(orderDto ->
                                Pair.of(orderDto.getMapPoint(), orderDto.getClientName()),
                        LinkedHashMap::new, Collectors.toList()));

        List<RoutePointDto> routePointDtoList = new ArrayList<>(1);
        addressOrderMap.forEach((addressClientPair, orderList) -> {
            List<RoutePointDto.AddressOrderDto> addressOrderDtos = mapOrdersAddress(orderList);
            double addRessTotalWeight = addressOrderDtos.stream()
                    .map(RoutePointDto.AddressOrderDto::getOrderTotalWeight)
                    .collect(Collectors.summarizingDouble(amount -> amount)).getSum();

            RoutePointDto routePointDto = new RoutePointDto();
            routePointDto.setId(UUID.randomUUID());
            routePointDto.setStatus(RoutePointStatus.PENDING);
            routePointDto.setAddress(addressClientPair.getFirst().getAddress());
            routePointDto.setClientName(addressClientPair.getSecond());
            routePointDto.setOrders(addressOrderDtos);
            routePointDto.setAddressTotalWeight(addRessTotalWeight);
            routePointDto.setMapPoint(addressClientPair.getFirst());
            routePointDtoList.add(routePointDto);
        });
        return routePointDtoList;
    }

    private List<RoutePointDto.AddressOrderDto> mapOrdersAddress(List<OrderDto> orders) {
        return orders.stream()
                .map(this::mapOrderAddress)
                .collect(Collectors.toList());
    }

    private RoutePointDto.AddressOrderDto mapOrderAddress(OrderDto order) {
        List<ProductDto> products = order.getProducts();
        double orderTotalWeight = getOrderTotalWeight(products);

        RoutePointDto.AddressOrderDto addressOrderDto = new RoutePointDto.AddressOrderDto();
        addressOrderDto.setId(order.getId());
        addressOrderDto.setOrderNumber(String.valueOf(order.getOrderNumber()));
        addressOrderDto.setComment(order.getComment());
        addressOrderDto.setOrderTotalWeight(orderTotalWeight);

        return addressOrderDto;
    }

    private double getOrderTotalWeight(List<ProductDto> orderedProducts) {
        return BigDecimal.valueOf(orderedProducts.stream()
                .map(ProductDto::getTotalProductWeight)
                .collect(Collectors.summarizingDouble(weight -> weight)).getSum())
                .setScale(2, RoundingMode.HALF_UP).doubleValue();
    }
}
