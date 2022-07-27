package com.goods.partner.service;

import com.goods.partner.entity.Car;

public interface CarService {

    void addCar(Car car);

    void removeCar(int id);

    Car updateCarStatus(int id, String status);
}