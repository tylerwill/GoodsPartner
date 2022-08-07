package com.goodspartner.mapper;

import com.goodspartner.dto.CarDto;
import com.goodspartner.entity.Car;
import org.mapstruct.Mapper;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface CarMapper {
    List<CarDto> carsToCarDtos(List<Car> cars);

    CarDto carToCarDto(Car car);

    Car carDtoToCar(CarDto carDto);
}
