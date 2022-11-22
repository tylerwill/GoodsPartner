package com.goodspartner.service.impl;

import com.goodspartner.cache.CarLocationCache;
import com.goodspartner.dto.CarDto;
import com.goodspartner.entity.Car;
import com.goodspartner.dto.Location;
import com.goodspartner.exception.CarNotFoundException;
import com.goodspartner.mapper.CarMapper;
import com.goodspartner.repository.CarRepository;
import com.goodspartner.service.CarService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class DefaultCarService implements CarService {
    private final CarLocationCache carLocationCache;
    private final CarRepository carRepository;
    private final CarMapper carMapper;

    @Override
    @Transactional(readOnly = true)
    public List<CarDto> findAll() {
        return carMapper.carsToCarDtos(carRepository.findAll());
    }

    @Override
    @Transactional(readOnly = true)
    public List<CarDto> findByAvailableCars() {
        return carMapper.carsToCarDtos(carRepository.findByAvailableTrue());
    }

    @Override
    @Transactional
    public CarDto update(int id, CarDto carDto) {
        Car updateCar = carRepository.findById(id)
                .map(car -> carMapper.update(car, carDto))
                .orElseThrow(() -> new CarNotFoundException("Car not found"));
        return carMapper.carToCarDto(carRepository.save(updateCar));
    }

    @Override
    public CarDto add(CarDto carDto) {
        Car car = carMapper.carDtoToCar(carDto);
        return carMapper.carToCarDto(carRepository.save(car));
    }

    @Override
    public Car findById(int id) {
        return carRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("No car for id: " + id));
    }

    @Override
    public void delete(int id) {
        carRepository.deleteById(id);
    }

    @Override
    public void saveCarLocation(int id, Location location) {
        carLocationCache.saveLocation(id, location);
    }

    @Override
    public Location getCarLocation(int id) {
        return carLocationCache.getLocation(id);
    }
}