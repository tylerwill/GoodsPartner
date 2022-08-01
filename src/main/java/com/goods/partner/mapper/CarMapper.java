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
        carDto.setLicencePlate(car.getLicencePlate());
        carDto.setWeightCapacity(car.getWeightCapacity());
        carDto.setCooler(car.isCooler());
        carDto.setStatus(car.isStatus());
        return carDto;
    }

    public Car mapCar(CarDto carDto) {
        Car car = new Car();
        car.setId(carDto.getId());
        car.setName(carDto.getName());
        car.setDriver(carDto.getDriver());
        car.setLicencePlate(carDto.getLicencePlate());
        car.setWeightCapacity(carDto.getWeightCapacity());
        car.setCooler(carDto.isCooler());
        car.setStatus(carDto.isStatus());
        return car;
    }
}
