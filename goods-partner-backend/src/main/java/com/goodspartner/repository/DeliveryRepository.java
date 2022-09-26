package com.goodspartner.repository;

import com.goodspartner.entity.Delivery;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface DeliveryRepository extends JpaRepository<Delivery, UUID> {

    Optional<Delivery> findByDeliveryDate(LocalDate date);

    List<Delivery> findByDeliveryDateBetween(LocalDate deliveryDate, LocalDate deliveryDate2);

}
