package com.goodspartner.mapper;

import com.goodspartner.dto.OrderDto;
import com.goodspartner.entity.CarLoad;
import com.goodspartner.entity.OrderExternal;
import com.goodspartner.web.controller.response.RoutesCalculation;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public abstract class CarLoadMapper {
    @Autowired
    private OrderExternalMapper orderExternalMapper;

    public abstract List<CarLoad> toCarLoads(List<RoutesCalculation.CarLoadDto> carLoadDtos);

    public abstract List<RoutesCalculation.CarLoadDto> toCarLoadDtos(List<CarLoad> carLoads);

    @Mapping(target = "orders", source = "orders", qualifiedByName = "mapOrders")
    public abstract CarLoad carLoadDtoToCarLoad(RoutesCalculation.CarLoadDto carLoadDto);

    @Mapping(target = "orders", source = "orders", qualifiedByName = "mapOrdersDto")
    @Mapping(target = "car", source = "car")
    @Mapping(target = "car.loadSize", source = "orders", qualifiedByName = "mapCarLoadSize")
    public abstract RoutesCalculation.CarLoadDto carLoadToCarLoadDto(CarLoad carLoad);

    @Named("mapCarLoadSize")
    double mapCarLoadSize(List<OrderExternal> order) {
        return BigDecimal.valueOf(order.stream()
                        .map(OrderExternal::getOrderWeight)
                        .collect(Collectors.summarizingDouble(s -> s)).getSum())
                .setScale(2, RoundingMode.HALF_UP).doubleValue();
    }

    @Named("mapOrders")
    List<OrderExternal> mapOrders(List<OrderDto> orderDtos) {
        return orderExternalMapper.mapOrderDtosToOrderExternal(orderDtos);
    }

    @Named("mapOrdersDto")
    List<OrderDto> mapOrdersDto(List<OrderExternal> orders) {
        return orderExternalMapper.mapExternalOrdersToOrderDtos(orders);
    }
}
