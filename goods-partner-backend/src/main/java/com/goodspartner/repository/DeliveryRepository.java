package com.goodspartner.repository;

import com.goodspartner.entity.Delivery;
import com.goodspartner.entity.DeliveryStatus;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
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
    Optional<Delivery> findById(UUID id);

}
