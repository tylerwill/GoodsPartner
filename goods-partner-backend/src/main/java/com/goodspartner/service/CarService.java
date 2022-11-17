package com.goodspartner.service;

import com.goodspartner.dto.CarDto;
import com.goodspartner.dto.Location;

import java.util.List;

public interface CarService {

    CarDto add(CarDto carDto);

    void delete(int id);

    CarDto update(int id, CarDto car);

    CarDto findById(int id);

    List<CarDto> findAll();

    List<CarDto> findByAvailableCars();

    void saveCarLocation(int id, Location location);

    Location getCarLocation(int id);
}