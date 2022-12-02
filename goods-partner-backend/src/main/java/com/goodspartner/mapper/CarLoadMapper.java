package com.goodspartner.mapper;

import com.goodspartner.dto.CarLoadDto;
import com.goodspartner.entity.CarLoad;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = OrderExternalMapper.class)
public interface CarLoadMapper {

    CarLoad toCarLoad(CarLoadDto carLoadDto);

    CarLoadDto toCarLoadDto(CarLoad carLoad);

}
