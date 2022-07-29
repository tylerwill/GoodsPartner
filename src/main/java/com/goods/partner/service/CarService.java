package com.goods.partner.service;

import com.goods.partner.dto.CarDto;
import com.goods.partner.entity.Car;
import com.goods.partner.entity.CarStatus;

import java.util.List;

public interface CarService {

    void addCar(Car car);

    void removeCar(int id);

    CarDto updateCarStatus(int id, CarStatus status);

    CarDto getCarById(int id);

    List<CarDto> getAllCars();
}