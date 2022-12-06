package com.goodspartner.repository;

import com.goodspartner.entity.Car;
import com.goodspartner.entity.Delivery;
import com.goodspartner.entity.DeliveryStatus;
import org.springframework.data.domain.Sort;
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

    @EntityGraph(attributePaths = {"orders"})
    @Query("SELECT d FROM Delivery d WHERE d.id = :id")
    Optional<Delivery> findByIdWithOrders(UUID id);

    @Query("SELECT d FROM Delivery d JOIN d.carLoads c WHERE c.car = :car " +
            "AND d.status = 'APPROVED' OR d.status = 'COMPLETED'")
    List<Delivery> findDeliveriesByCarAndStatus(@Param("car") Car car, Sort sortByDeliveryDate);
}
