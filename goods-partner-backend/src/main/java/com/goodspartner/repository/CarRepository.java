package com.goodspartner.repository;

import com.goodspartner.entity.Car;
import com.goodspartner.entity.User;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CarRepository extends JpaRepository<Car, Integer> {

    List<Car> findByAvailableTrue();

    List<Car> findByAvailableTrueAndCoolerIs(boolean coolerRequired);

    Optional<Car> findCarByDriver(User user);

    @EntityGraph(attributePaths = {"driver"})
    Optional<Car> findById(int id);

    @EntityGraph(attributePaths = {"driver"})
    List<Car> findAll(Sort sort);
}