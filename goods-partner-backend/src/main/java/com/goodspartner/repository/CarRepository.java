package com.goodspartner.repository;

import com.goodspartner.entity.Car;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CarRepository extends JpaRepository<Car, Integer> {

    List<Car> findByAvailableTrue();

    List<Car> findByAvailableTrueAndCoolerIs(boolean coolerRequired);

    Car findCarByDriver(String userName);
}