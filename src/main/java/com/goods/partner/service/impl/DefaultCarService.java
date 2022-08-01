package com.goods.partner.service.impl;

import com.goods.partner.dto.CarDto;
import com.goods.partner.entity.Car;
import com.goods.partner.mapper.CarMapper;
import com.goods.partner.repository.CarRepository;
import com.goods.partner.service.CarService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

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
    @Transactional
    public void updateStatus(int id, boolean status) {
        carRepository.updateStatus(id, status);
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