package com.goods.partner.service.impl;

import com.goods.partner.entity.Car;
import com.goods.partner.repository.CarRepository;
import com.goods.partner.service.CarService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class CarServiceImpl implements CarService {

    private final CarRepository carRepository;

    @Override
    @Transactional
    public void createCar(Car car) {
        carRepository.save(car);
    }

    @Override
    @Transactional
    public void removeCar(int id) {
        carRepository.deleteById(id);
    }

    @Override
    @Transactional
    public Car updateCarStatus(int id, String status) {
        Car car = carRepository.findById(id).get();
        car.setStatus(car.getStatus());
        return carRepository.save(car);
    }
}