package com.goods.partner.mapper;

import com.goods.partner.dto.CarDto;
import com.goods.partner.entity.Car;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class CarMapper {
    public List<CarDto> mapCars(List<Car> cars) {
        return cars.stream()
                .map(this::mapCar)
                .collect(Collectors.toList());
    }

    public CarDto mapCar(Car car) {
        CarDto carDto = new CarDto();
        carDto.setId(car.getId());
        carDto.setName(car.getName());
        carDto.setDriver(car.getDriver());
        carDto.setLicence_plate(car.getLicence_plate());
        carDto.setWeight_capacity(car.getWeight_capacity());
        carDto.setStatus(car.getStatus());

        return carDto;
    }
}
