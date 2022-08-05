package com.goodspartner.web.controller;

import com.goodspartner.dto.CarDto;
import com.goodspartner.service.CarService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/cars")
public class CarController {
    private final CarService carService;

    @GetMapping
    public List<CarDto> getAll() {
        return carService.findAll();
    }

    @PutMapping("/{id}")
    public void update(@PathVariable int id, @RequestBody CarDto car) {
        carService.update(id, car);
    }

    @PostMapping()
    public void add(@RequestBody CarDto car) {
        carService.add(car);
    }

    @DeleteMapping("{id}")
    public void delete(@PathVariable("id") int id) {
        carService.delete(id);
    }

    @GetMapping("/{id}")
    public CarDto getById(@PathVariable("id") int id) {
        return carService.getById(id);
    }
}