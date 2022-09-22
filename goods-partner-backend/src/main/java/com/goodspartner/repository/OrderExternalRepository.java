package com.goodspartner.repository;

import com.goodspartner.entity.OrderExternal;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Repository
public interface OrderExternalRepository extends JpaRepository<OrderExternal, LocalDate> {

    @EntityGraph(attributePaths = {"addressExternal"})
    List<OrderExternal> findAllByDeliveryId(UUID id);
}
