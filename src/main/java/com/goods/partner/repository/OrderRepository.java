package com.goods.partner.repository;

import com.goods.partner.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;

@Repository
public interface OrderRepository extends JpaRepository<Order, LocalDate> {
    @Query("SELECT o FROM Order WHERE o.date = ?1")
    Optional<Order> findStudentByEmail(String email);
}
