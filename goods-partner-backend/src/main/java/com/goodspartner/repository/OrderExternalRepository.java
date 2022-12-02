package com.goodspartner.repository;

import com.goodspartner.entity.Car;
import com.goodspartner.entity.OrderExternal;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface OrderExternalRepository extends JpaRepository<OrderExternal, Integer> {

    @EntityGraph(attributePaths = {"addressExternal"})
    @Query("SELECT o FROM OrderExternal o WHERE o.delivery.id = :deliveryId")
    List<OrderExternal> findByDeliveryId(@Param("deliveryId") UUID deliveryId);

    @EntityGraph(attributePaths = {"addressExternal"})
    @Query("SELECT o FROM OrderExternal o WHERE o.delivery.id = :deliveryId AND o.carLoad.car = :car")
    List<OrderExternal> findAllByDeliveryAndCar(@Param("deliveryId") UUID deliveryId,
                                                @Param("car") Car car);

    @EntityGraph(attributePaths = {"addressExternal"})
    @Query("SELECT o FROM OrderExternal o LEFT JOIN o.routePoint rp " +
            "WHERE o.rescheduleDate IS NULL " +
            "  AND (o.excluded = TRUE OR o.dropped = TRUE OR rp.status = 'SKIPPED')")
    List<OrderExternal> findSkippedOrders();

    @EntityGraph(attributePaths = {"addressExternal"})
    @Query("SELECT o FROM OrderExternal o " +
            "WHERE o.excluded = FALSE AND o.dropped = FALSE AND o.routePoint.status = 'DONE'")
    List<OrderExternal> findCompletedOrders();

    // Order has not been linked with delivery
    @EntityGraph(attributePaths = {"addressExternal"})
    @Query("SELECT o FROM OrderExternal o " +
            "WHERE o.rescheduleDate IS NOT NULL AND o.delivery IS NULL")
    List<OrderExternal> findScheduledOrders();

    List<OrderExternal> findByRescheduleDate(LocalDate date);

    @EntityGraph(attributePaths = {"addressExternal"})
    @Query("SELECT o FROM OrderExternal o WHERE o.id IN :orderIds")
    List<OrderExternal> findByOrderIds(@Param("orderIds") List<Integer> orderIds);

    @Override
    @EntityGraph(attributePaths = {"addressExternal", "delivery"})
    @Query("SELECT o FROM OrderExternal o WHERE o.id = :id")
    Optional<OrderExternal> findById(@Param("id") Integer id);
}
