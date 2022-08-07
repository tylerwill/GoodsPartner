package com.goodspartner.repository;

import com.goodspartner.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, LocalDate> {

    // TODO require join query to avoid data enrichment
//    @Query("SELECT o FROM Order WHERE o.date = ?1")
    List<Order> findAllByShippingDateEquals(LocalDate date);
}
