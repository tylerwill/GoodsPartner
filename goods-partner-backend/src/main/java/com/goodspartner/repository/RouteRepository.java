package com.goodspartner.repository;

import com.goodspartner.entity.Car;
import com.goodspartner.entity.Delivery;
import com.goodspartner.entity.Route;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RouteRepository extends JpaRepository<Route, Integer> {

    @EntityGraph(attributePaths = {"store"})
    List<Route> findByDeliveryAndCar(Delivery delivery, Car car);

}
