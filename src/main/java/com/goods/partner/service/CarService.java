package com.goods.partner.service;

import com.goods.partner.dto.CarDto;

import java.util.List;

public interface CarService {

    void add(CarDto carDto);

    void delete(int id);

    void update(int id, boolean available);

    CarDto getById(int id);

    List<CarDto> findAll();
}