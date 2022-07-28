package com.goods.partner.service;

import com.goods.partner.entity.Car;
import com.goods.partner.entity.CarStatus;

public interface CarService {

    void addCar(Car car);

    void removeCar(int id);

    Car updateCarStatus(int id, CarStatus status);
}