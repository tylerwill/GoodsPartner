package com.goodspartner.repository;

import com.goodspartner.entity.Car;
import com.goodspartner.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CarRepository extends JpaRepository<Car, Integer> {

    List<Car> findByAvailableTrue();

    List<Car> findByAvailableTrueAndCoolerIs(boolean coolerRequired);

    @Query("SELECT c FROM Car c WHERE c.driver = :#{#user.userName}")
    Car findCarByDriver(@Param("user") User user);
}