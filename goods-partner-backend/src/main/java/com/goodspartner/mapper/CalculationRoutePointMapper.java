package com.goodspartner.mapper;

import com.goodspartner.dto.OrderDto;
import com.goodspartner.dto.ProductDto;
import com.goodspartner.dto.RoutePointDto;
import com.goodspartner.entity.RoutePointStatus;
import com.google.common.annotations.VisibleForTesting;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class CalculationRoutePointMapper {

    /**
     * TODO refactor Pair<Address, Client> by introducing separate AddressDto which will encapsulates the data
     *      and will be included in OrderDto
     */
    public List<RoutePointDto> mapOrders(List<OrderDto> orders) {
        Map<Pair<String, String>, List<OrderDto>> addressOrderMap = orders.stream()
                .collect(Collectors.groupingBy(orderDto ->
                                Pair.of(orderDto.getAddress(), orderDto.getClientName()),
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
            routePointDto.setAddress(addressClientPair.getFirst());
//            routePointDto.setClientId(addressClientPair.getClient().getId()); TOOD Refactoring
            routePointDto.setClientName(addressClientPair.getSecond());
            routePointDto.setOrders(addressOrderDtos);
            routePointDto.setAddressTotalWeight(addRessTotalWeight * 50);
            routePointDtoList.add(routePointDto);
        });
        return routePointDtoList;
    }

    @VisibleForTesting
    List<RoutePointDto.AddressOrderDto> mapOrdersAddress(List<OrderDto> orders) {
        return orders.stream()
                .map(this::mapOrderAddress)
                .collect(Collectors.toList());
    }

    @VisibleForTesting
    RoutePointDto.AddressOrderDto mapOrderAddress(OrderDto order) {
        List<ProductDto> products = order.getProducts();
        double orderTotalWeight = getOrderTotalWeight(products);

        RoutePointDto.AddressOrderDto addressOrderDto = new RoutePointDto.AddressOrderDto();
        addressOrderDto.setId(order.getId());
        addressOrderDto.setOrderNumber(String.valueOf(order.getOrderNumber()));
        addressOrderDto.setOrderTotalWeight(orderTotalWeight);

        return addressOrderDto;
    }

    // TODO: I think this is not mapper class, but class for calculations
    @VisibleForTesting
    double getOrderTotalWeight(List<ProductDto> orderedProducts) {
        return BigDecimal.valueOf(orderedProducts.stream()
                        .map(orderedProduct -> orderedProduct.getAmount() * orderedProduct.getUnitWeight())
                        .collect(Collectors.summarizingDouble(amount -> amount)).getSum())
                .setScale(2, RoundingMode.HALF_UP).doubleValue();
    }
}
