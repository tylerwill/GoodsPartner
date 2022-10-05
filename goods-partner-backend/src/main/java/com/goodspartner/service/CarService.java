package com.goodspartner.service;

import com.goodspartner.dto.CarDto;
import com.goodspartner.dto.Location;

import java.util.List;

public interface CarService {

    void add(CarDto carDto);

    void delete(int id);

    void update(int id, CarDto car);

    CarDto getById(int id);

    List<CarDto> findAll();

    List<CarDto> findByAvailableCars();

    void saveCarLocation(int id, Location location);

    Location getCarLocation(int id);
}