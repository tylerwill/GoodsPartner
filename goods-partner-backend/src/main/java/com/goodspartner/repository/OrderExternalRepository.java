package com.goodspartner.repository;

import com.goodspartner.entity.OrderExternal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;

@Repository
public interface OrderExternalRepository extends JpaRepository<OrderExternal, LocalDate> {

}
