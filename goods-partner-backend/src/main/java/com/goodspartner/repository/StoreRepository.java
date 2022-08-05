package com.goodspartner.repository;

import com.goodspartner.entity.Store;
import com.goodspartner.entity.projection.StoreProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface StoreRepository extends JpaRepository<Store, LocalDate> {

    @Query("SELECT new com.goods.partner.entity.projection.StoreProjection(s.id, s.name, s.address, o.id, o.number, SUM(op.count * p.kg)) " +
            " FROM Order o JOIN o.orderedProducts op JOIN op.product p JOIN p.store s " +
            " WHERE o.shippingDate = :date " +
            " GROUP BY s.id, s.name, s.address, o.id, o.number")
    List<StoreProjection> groupStoresByOrders(LocalDate date);
}
