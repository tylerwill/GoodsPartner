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
    public List<CarDto> getAllCars() {
        return carService.findAll();
    }

    @PutMapping("/update/{id}")
    public void updateCarStatus(@PathVariable int id, @RequestParam boolean status) {
        carService.updateStatus(id, status);
    }

    @PostMapping("/add")
    public void addCar(@RequestBody CarDto car) {
        carService.add(car);
    }

    @DeleteMapping("/delete/{id}")
    public void deleteCar(@PathVariable("id") int id) {
        carService.delete(id);
    }

    @GetMapping("/get/{id}")
    public CarDto getCarById(@PathVariable("id") int id) {
        return carService.getById(id);
    }
}