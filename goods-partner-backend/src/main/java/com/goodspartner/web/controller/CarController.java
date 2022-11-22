package com.goodspartner.web.controller;

import com.goodspartner.dto.CarDto;
import com.goodspartner.dto.Location;
import com.goodspartner.mapper.CarMapper;
import com.goodspartner.service.CarService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/api/v1/cars", produces = MediaType.APPLICATION_JSON_VALUE)
public class CarController {

    private final CarService carService;

    private final CarMapper carMapper;

    @PreAuthorize("hasAnyRole('ADMIN', 'DRIVER', 'LOGIST')")
    @GetMapping
    @ApiOperation(value = "Get all Cars",
            notes = "Return list of CarDto",
            response = List.class)
    public List<CarDto> getAll() {
        return carService.findAll();
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'LOGIST', 'DRIVER')")
    @GetMapping("/{id}")
    @ApiOperation(value = "Find Car by id",
            notes = "Provide an id to look up specific car",
            response = CarDto.class)
    public CarDto getById(@ApiParam(value = "ID value for the car you need to retrieve", required = true)
                          @PathVariable("id") int id) {
        return carMapper.carToCarDto(carService.findById(id));
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'LOGIST')")
    @PostMapping()
    @ApiOperation(value = "Add car")
    public CarDto add(@ApiParam(value = "CarDto that you want to add", type = "CarDto", required = true)
                      @RequestBody CarDto car) {
        return carService.add(car);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'LOGIST')")
    @PutMapping("/{id}")
    @ApiOperation(value = "Edit Car",
            notes = "Provide an id to edit up specific car")
    public CarDto update(@ApiParam(value = "ID of edited Car", required = true)
                         @PathVariable int id,
                         @ApiParam(value = "Edited CarDto", type = "CarDto", required = true)
                         @RequestBody CarDto car) {
        return carService.update(id, car);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'LOGIST')")
    @DeleteMapping("{id}")
    @ApiOperation(value = "Remove Car by id",
            notes = "Provide an id to remove up specific car")
    public void delete(@ApiParam(value = "ID of the car to delete", required = true)
                       @PathVariable("id") int id) {
        carService.delete(id);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'LOGIST')")
    @PutMapping("/{id}/location")
    @ApiOperation(value = "Save car location",
            notes = "Provide an id to save the location of the car")
    public void saveLocation(@ApiParam(value = "ID value for the car you need to save", required = true)
                             @PathVariable int id,
                             @ApiParam(value = "Car Location", type = "Location", required = true)
                             @RequestBody Location location) {
        carService.saveCarLocation(id, location);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'LOGIST')")
    @GetMapping("/{id}/location")
    @ApiOperation(value = "Get car location",
            notes = "Provide an id to determine the location of the car")
    public Location getLocation(@ApiParam(value = "ID value for the car you need to determine", required = true)
                                @PathVariable int id) {
        return carService.getCarLocation(id);
    }
}