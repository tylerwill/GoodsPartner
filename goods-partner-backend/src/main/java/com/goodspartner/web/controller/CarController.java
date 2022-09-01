package com.goodspartner.web.controller;

import com.goodspartner.dto.CarDto;
import com.goodspartner.service.CarService;
import com.goodspartner.security.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/cars")
public class CarController {

    private final CarService carService;

    @PreAuthorize("hasAnyRole('ADMIN', 'DRIVER', 'LOGIST')")
    @GetMapping
    public List<CarDto> getAll() {
        return carService.findAll();
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'LOGIST')")
    @PutMapping("/{id}")
    public void update(@PathVariable int id, @RequestBody CarDto car) {
        carService.update(id, car);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'LOGIST')")
    @PostMapping()
    public void add(@RequestBody CarDto car) {
        carService.add(car);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'LOGIST')")
    @DeleteMapping("{id}")
    public void delete(@PathVariable("id") int id) {
        carService.delete(id);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'LOGIST', 'DRIVER')")
    @GetMapping("/{id}")
    public CarDto getById(@PathVariable("id") int id) {
        return carService.getById(id);
    }
}