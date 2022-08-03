package com.goods.partner.web.controller;

import com.goods.partner.dto.CarDto;
import com.goods.partner.service.CarService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/cars")
public class CarController {
    private final CarService carService;

    @GetMapping("/all")
    public List<CarDto> getAll() {
        return carService.findAll();
    }

    @PutMapping("/update/{id}")
    public void update(@PathVariable int id, @RequestBody CarDto car) {
        carService.update(id, car);
    }

    @PostMapping("/add")
    public void add(@RequestBody CarDto car) {
        carService.add(car);
    }

    @DeleteMapping("/delete/{id}")
    public void delete(@PathVariable("id") int id) {
        carService.delete(id);
    }

    @GetMapping("/get/{id}")
    public CarDto getById(@PathVariable("id") int id) {
        return carService.getById(id);
    }
}