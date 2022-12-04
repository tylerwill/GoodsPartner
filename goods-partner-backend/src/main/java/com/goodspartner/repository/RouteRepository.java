package com.goodspartner.repository;

import com.goodspartner.entity.Car;
import com.goodspartner.entity.Route;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface RouteRepository extends JpaRepository<Route, Long> {

    @EntityGraph(attributePaths = {"routePoints", "routePoints.addressExternal", "store", "car.driver"})
    List<Route> findByDeliveryIdAndCar(UUID deliveryId, Car car);

    @EntityGraph(attributePaths = {"routePoints", "routePoints.addressExternal", "store", "car.driver"})
    @Query(value = "SELECT r FROM Route r WHERE r.delivery.id = :id")
    List<Route> findByDeliveryIdExtended(@Param("id") UUID deliveryId);

    List<Route> findByDeliveryId(@Param("id") UUID deliveryId);
}
