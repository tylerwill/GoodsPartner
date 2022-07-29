package com.goods.partner.web.controller;

import com.goods.partner.dto.CarDto;
import com.goods.partner.entity.Car;
import com.goods.partner.entity.CarStatus;
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
        return carService.getAllCars();
    }

    @PutMapping("/update/{id}")
    public CarDto updateCarStatus(@PathVariable int id, @RequestParam String status) {
        return carService.updateCarStatus(id, CarStatus.getCarStatus(status));
    }

    @PostMapping("/add")
    public void addCar(@RequestBody Car car) {
        carService.addCar(car);
    }

    @DeleteMapping("/delete/{id}")
    public void deleteCar(@PathVariable("id") int id) {
        carService.removeCar(id);
    }

    @GetMapping("/get/{id}")
    public CarDto getCarById(@PathVariable("id") int id) {
        return carService.getCarById(id);
    }
}