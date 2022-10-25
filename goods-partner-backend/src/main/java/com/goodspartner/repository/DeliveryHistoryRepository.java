package com.goodspartner.repository;

import com.goodspartner.entity.Delivery;
import com.goodspartner.entity.DeliveryHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface DeliveryHistoryRepository extends JpaRepository<DeliveryHistory, UUID> {

    List<DeliveryHistory> findByDeliveryOrderByCreatedAtDesc(Delivery delivery);
}
