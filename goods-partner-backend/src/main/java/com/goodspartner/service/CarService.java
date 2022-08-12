package com.goodspartner.service;

import com.goodspartner.dto.CarDto;

import java.util.List;

public interface CarService {

    void add(CarDto carDto);

    void delete(int id);

    void update(int id, CarDto car);

    CarDto getById(int id);

    List<CarDto> findAll();

    List<CarDto> findByAvailableCars();
}