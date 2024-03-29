package com.goodspartner.repository;

import com.goodspartner.entity.Car;
import com.goodspartner.entity.CarLoad;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface CarLoadRepository extends JpaRepository<CarLoad, UUID> {

    @EntityGraph(attributePaths = {"orders", "car", "car.driver"})
    List<CarLoad> findByDeliveryIdAndCar(UUID deliveryId, Car car);

    @EntityGraph(attributePaths = {"orders", "car", "car.driver"})
    List<CarLoad> findByDeliveryId(UUID id);

}
