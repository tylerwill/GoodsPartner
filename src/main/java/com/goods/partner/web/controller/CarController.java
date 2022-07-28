package com.goods.partner.web.controller;

import com.goods.partner.entity.Car;
import com.goods.partner.entity.CarStatus;
import com.goods.partner.service.CarService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/cars")
public class CarController {
    private final CarService carService;

    @PostMapping("/add")
    public void createCar(@RequestBody Car car) {
        carService.addCar(car);
    }

    @DeleteMapping("/delete/{id}")
    public void deleteCar(@PathVariable("id") int id) {
        carService.removeCar(id);
    }

    @PutMapping("/update/{id}")
    public Car update(@PathVariable int id, @RequestParam String status) {
        return carService.updateCarStatus(id, CarStatus.getCarStatus(status));
    }
}