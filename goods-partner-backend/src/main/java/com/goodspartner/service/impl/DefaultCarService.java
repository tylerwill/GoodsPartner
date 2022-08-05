package com.goodspartner.service.impl;

import com.goodspartner.dto.CarDto;
import com.goodspartner.entity.Car;
import com.goodspartner.exceptions.CarNotFoundException;
import com.goodspartner.mapper.CarMapper;
import com.goodspartner.repository.CarRepository;
import com.goodspartner.service.CarService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class DefaultCarService implements CarService {
    @PersistenceContext
    private EntityManager entityManager;
    private final CarRepository carRepository;
    private final CarMapper carMapper;

    @Override
    @Transactional(readOnly = true)
    public List<CarDto> findAll() {
        return carMapper.mapCars(carRepository.findAll());
    }

    @Override
    public List<CarDto> findByAvailableCars() {
        return carMapper.mapCars(carRepository.findByAvailableTrue());
    }

    @Override
    @Transactional
    public void update(int id, CarDto car) {
        Optional<Car> optionalCar = carRepository.findById(id);
        if (optionalCar.isEmpty()) {
            throw new CarNotFoundException("Car not found");
        }
        Car dbCar = optionalCar.get();
        Car updatedCar = carMapper.mapCar(car);

        if (Objects.nonNull(updatedCar.getName()) &&
                !"".equalsIgnoreCase(updatedCar.getName())) {
            dbCar.setName(updatedCar.getName());
        }
        if (Objects.nonNull(updatedCar.getDriver()) &&
                !"".equalsIgnoreCase(updatedCar.getDriver())) {
            dbCar.setDriver(updatedCar.getDriver());
        }
        if (Objects.nonNull(updatedCar.getLicencePlate()) &&
                !"".equalsIgnoreCase(updatedCar.getLicencePlate())) {
            dbCar.setLicencePlate(updatedCar.getLicencePlate());
        }
        if (Objects.nonNull(updatedCar.getAvailable())){
            dbCar.setAvailable(updatedCar.getAvailable());
        }
        if (Objects.nonNull(updatedCar.getCooler())){
            dbCar.setCooler(updatedCar.getCooler());
        }
        if (updatedCar.getTravelCost() > 0) {
            dbCar.setTravelCost(updatedCar.getTravelCost());
        }
        if (updatedCar.getWeightCapacity() > 0) {
            dbCar.setWeightCapacity(updatedCar.getWeightCapacity());
        }
        carRepository.save(dbCar);
    }

    @Override
    public void add(CarDto carDto) {
        Car car = carMapper.mapCar(carDto);
        carMapper.mapCar(carRepository.save(car));
    }

    @Override
    public CarDto getById(int id) {
        return carMapper.mapCar(entityManager.getReference(Car.class, id));
    }

    @Override
    public void delete(int id) {
        carRepository.deleteById(id);
    }
}