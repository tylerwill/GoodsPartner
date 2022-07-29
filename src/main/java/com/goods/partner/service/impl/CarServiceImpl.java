package com.goods.partner.service.impl;

import com.goods.partner.dto.CarDto;
import com.goods.partner.entity.Car;
import com.goods.partner.entity.CarStatus;
import com.goods.partner.mapper.CarMapper;
import com.goods.partner.repository.CarRepository;
import com.goods.partner.service.CarService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class CarServiceImpl implements CarService {

    private final CarRepository carRepository;
    private final CarMapper carMapper;
    private List<CarDto> carDtos;

    @Override
    @Transactional
    public List<CarDto> getAllCars() {
        List<Car> cars = carRepository.findAll();
        carDtos = carMapper.mapCars(cars);
        return carDtos;
    }

    @Override
    @Transactional
    public CarDto updateCarStatus(int id, CarStatus status) {
        Car car = carRepository.findById(id).get();
        car.setStatus(status);
        carRepository.save(car);

        return carMapper.mapCar(carRepository.save(car));
    }

    @Override
    @Transactional
    public void addCar(Car car) {
        carMapper.mapCar(carRepository.save(car));
    }

    @Override
    @Transactional
    public CarDto getCarById(int id) {
        Car car = carRepository.findById(id).get();
        return carMapper.mapCar(car);
    }

    @Override
    @Transactional
    public void removeCar(int id) {
        carDtos.remove(carDtos.get(id));
        carRepository.deleteById(id);
    }
}