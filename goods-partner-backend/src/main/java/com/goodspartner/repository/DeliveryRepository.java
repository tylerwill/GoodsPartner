package com.goodspartner.repository;

import com.goodspartner.entity.Car;
import com.goodspartner.entity.Delivery;
import com.goodspartner.entity.DeliveryStatus;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface DeliveryRepository extends JpaRepository<Delivery, UUID> {

    Optional<Delivery> findByDeliveryDate(LocalDate date);

    @EntityGraph(attributePaths = {"routes", "routes.car", "routes.store"})
    Optional<Delivery> findByStatusAndDeliveryDate(DeliveryStatus status, LocalDate date);

    @EntityGraph(attributePaths = {"routes", "routes.car", "routes.store"})
    List<Delivery> findByStatusAndDeliveryDateBetween(DeliveryStatus status, LocalDate dateFrom, LocalDate dateTo);

    @EntityGraph(attributePaths = {"orders.addressExternal"})
    Optional<Delivery> findById(UUID id);

    @Query("SELECT d FROM Delivery d JOIN CarLoad c ON d.id = c.delivery WHERE c.car = :car")
    List<Delivery> findDeliveriesByCar(@Param("car") Car car);
}
