package com.goodspartner.mapper;

import com.goodspartner.dto.CarDto;
import com.goodspartner.entity.Car;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CarMapper {
    List<CarDto> toCarDtosList(List<Car> cars);

    CarDto toCarDto(Car car);

    Car toCar(CarDto carDto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "driver", ignore = true)
    Car update(@MappingTarget Car car, CarDto carDto);
}
