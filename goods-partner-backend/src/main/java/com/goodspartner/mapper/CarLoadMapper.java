package com.goodspartner.mapper;

import com.goodspartner.dto.CarLoadDto;
import com.goodspartner.entity.CarLoad;
import com.goodspartner.entity.OrderExternal;
import org.mapstruct.Mapper;
import org.mapstruct.Named;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", uses = OrderExternalMapper.class)
public interface CarLoadMapper {

    CarLoad carLoadDtoToCarLoad(CarLoadDto carLoadDto);

    @Named("mapCarLoadSize")
    default double mapCarLoadSize(List<OrderExternal> orders) {
        return BigDecimal.valueOf(orders.stream()
                        .map(OrderExternal::getOrderWeight)
                        .collect(Collectors.summarizingDouble(s -> s)).getSum())
                .setScale(2, RoundingMode.HALF_UP).doubleValue();
    }
}
