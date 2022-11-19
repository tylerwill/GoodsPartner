package com.goodspartner.repository;

import com.goodspartner.entity.Car;
import com.goodspartner.entity.Delivery;
import com.goodspartner.entity.Route;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface RouteRepository extends JpaRepository<Route, Integer> {

    @EntityGraph(attributePaths = {"store"})
    List<Route> findByDeliveryAndCar(Delivery delivery, Car car);

    @EntityGraph(attributePaths = {"routePoints", "store", "car"})
    @Query(value = "SELECT r FROM Route r WHERE r.delivery.id = :id")
    List<Route> findByDeliveryId(@Param("id") UUID deliveryId);
}
