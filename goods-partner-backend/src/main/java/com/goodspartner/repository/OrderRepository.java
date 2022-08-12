package com.goodspartner.repository;

import com.goodspartner.entity.Order;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, LocalDate> {

    // 2 queries in the end
    @EntityGraph(attributePaths = {"address.client", "manager"})
    List<Order> findAllByShippingDateEquals(LocalDate date);

}
