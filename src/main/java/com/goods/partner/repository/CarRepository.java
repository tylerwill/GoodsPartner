package com.goods.partner.repository;

import com.goods.partner.entity.Car;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface CarRepository extends JpaRepository<Car, Integer> {
    @Modifying
    @Query("update Car u set u.available = :available where u.id = :id")
    void updateStatus(@Param(value = "id") int id, @Param(value = "available") boolean available);
}
