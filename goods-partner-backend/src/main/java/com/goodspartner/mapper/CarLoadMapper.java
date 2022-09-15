package com.goodspartner.mapper;

import com.goodspartner.entity.CarLoad;
import com.goodspartner.web.controller.response.RoutesCalculation;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CarLoadMapper {

    CarLoad carLoadDtoToCarLoad(RoutesCalculation.CarLoadDto carLoadDto);

    RoutesCalculation.CarLoadDto carLoadDtoToCarLoad(CarLoad carLoad);

    List<CarLoad> toCarLoads(List<RoutesCalculation.CarLoadDto> carLoadDtos);

    List<RoutesCalculation.CarLoadDto> toCarLoadDtos(List<CarLoad> carLoads);
}
