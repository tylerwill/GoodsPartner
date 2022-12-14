package com.goodspartner.service.impl;

import com.goodspartner.cache.CarLocationCache;
import com.goodspartner.dto.CarDto;
import com.goodspartner.dto.Location;
import com.goodspartner.dto.UserDto;
import com.goodspartner.entity.Car;
import com.goodspartner.exception.CarNotFoundException;
import com.goodspartner.exception.UserNotFoundException;
import com.goodspartner.mapper.CarMapper;
import com.goodspartner.repository.CarRepository;
import com.goodspartner.repository.UserRepository;
import com.goodspartner.service.CarService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static com.goodspartner.entity.User.UserRole.DRIVER;

@Service
@RequiredArgsConstructor
@Slf4j
public class DefaultCarService implements CarService {
    private final CarLocationCache carLocationCache;
    private final CarRepository carRepository;
    private final UserRepository userRepository;
    private final CarMapper carMapper;

    @Override
    @Transactional(readOnly = true)
    public List<CarDto> findAll() {
        return carMapper.toCarDtosList(carRepository.findAll());
    }

    @Override
    @Transactional(readOnly = true)
    public List<CarDto> findByAvailableCars() {
        return carMapper.toCarDtosList(carRepository.findByAvailableTrue());
    }

    @Override
    @Transactional
    public CarDto update(int id, CarDto carDto) {
        log.info("Updating car by request payload: {}", carDto);
        Car updateCar = carRepository.findById(id)
                .map(car -> carMapper.update(car, carDto))
                .map(car -> updateDriver(car, carDto))
                .orElseThrow(() -> new CarNotFoundException("Car not found"));
        return carMapper.toCarDto(carRepository.save(updateCar));
    }

    private Car updateDriver(Car car, CarDto carDto) {
        return Optional.ofNullable(carDto.getDriver())
                .map(UserDto::getId)
                .map(userId -> userRepository.findById(userId)
                        .orElseThrow(() -> new UserNotFoundException(carDto.getDriver().getId())))
                .filter(driver -> DRIVER.equals(driver.getRole()))
                .map(driver -> {
                    car.setDriver(driver);
                    return car;
                })
                .orElseGet(() -> {
                    log.warn("Driver has not been set to car by request payload: {}", carDto);
                    return car;
                });
    }

    @Transactional
    @Override
    public CarDto add(CarDto carDto) {
        log.info("Adding new car by request payload: {}", carDto);
        Car car = carMapper.toCar(carDto);
        Car carWithDriver = updateDriver(car, carDto);
        return carMapper.toCarDto(carRepository.save(carWithDriver));
    }

    @Override
    public Car findById(int id) {
        return carRepository.findById(id)
                .orElseThrow(() -> new CarNotFoundException("No car for id: " + id));
    }

    @Override
    public void delete(int id) {
        log.info("Deleting car by id: {}", id);
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