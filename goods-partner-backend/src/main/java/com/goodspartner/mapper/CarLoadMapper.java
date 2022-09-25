package com.goodspartner.mapper;

import com.goodspartner.dto.CarLoadDto;
import com.goodspartner.entity.CarLoad;
import com.goodspartner.entity.OrderExternal;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", uses = OrderExternalMapper.class)
public interface CarLoadMapper {

    List<CarLoad> toCarLoads(List<CarLoadDto> carLoadDtos);

    List<CarLoadDto> toCarLoadDtos(List<CarLoad> carLoads);

    CarLoad carLoadDtoToCarLoad(CarLoadDto carLoadDto);

    @Mapping(target = "car", source = "car")
    @Mapping(target = "car.loadSize", source = "orders", qualifiedByName = "mapCarLoadSize")
    CarLoadDto carLoadToCarLoadDto(CarLoad carLoad);

    @Named("mapCarLoadSize")
    default double mapCarLoadSize(List<OrderExternal> orders) {
        return BigDecimal.valueOf(orders.stream()
                        .map(OrderExternal::getOrderWeight)
                        .collect(Collectors.summarizingDouble(s -> s)).getSum())
                .setScale(2, RoundingMode.HALF_UP).doubleValue();
    }
}
