package com.goodspartner.repository;

import com.goodspartner.entity.Car;
import com.goodspartner.entity.CarLoad;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface CarLoadRepository extends JpaRepository<CarLoad, UUID> {

    @EntityGraph(attributePaths = {"orders", "orders.addressExternal", "car"})
    List<CarLoad> findByDeliveryIdAndCar(UUID deliveryId, Car car);

    @EntityGraph(attributePaths = {"orders", "orders.addressExternal", "car"})
    List<CarLoad> findByDeliveryId(UUID id);

}
