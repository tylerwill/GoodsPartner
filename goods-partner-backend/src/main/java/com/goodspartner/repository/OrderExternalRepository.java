package com.goodspartner.repository;

import com.goodspartner.entity.Car;
import com.goodspartner.entity.OrderExternal;
import org.springframework.data.domain.Sort;
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
public interface OrderExternalRepository extends JpaRepository<OrderExternal, Long> {

    @EntityGraph(attributePaths = {"addressExternal"})
    @Query("SELECT o FROM OrderExternal o WHERE o.delivery.id = :deliveryId")
    List<OrderExternal> findByDeliveryId(@Param("deliveryId") UUID deliveryId, Sort sort);

    @EntityGraph(attributePaths = {"addressExternal"})
    @Query("SELECT o FROM OrderExternal o WHERE o.delivery.id = :deliveryId AND o.carLoad.car = :car")
    List<OrderExternal> findAllByDeliveryAndCar(@Param("deliveryId") UUID deliveryId,
                                                @Param("car") Car car, Sort sort);

    // For skipped order we do not set rescheduleDate yet
    @EntityGraph(attributePaths = {"addressExternal"})
    @Query("SELECT o FROM OrderExternal o LEFT JOIN o.routePoint rp " +
            "WHERE o.rescheduleDate IS NULL " +
            "  AND (o.excluded = TRUE OR o.dropped = TRUE OR rp.status = 'SKIPPED')")
    List<OrderExternal> findSkippedOrders(Sort sort);

    @EntityGraph(attributePaths = {"addressExternal"})
    @Query("SELECT o FROM OrderExternal o " +
            "WHERE o.excluded = FALSE " +
            "  AND o.dropped = FALSE " +
            "  AND o.routePoint.status = 'DONE'")
    List<OrderExternal> findCompletedOrders(Sort sort);

    // Scheduled orders doesn't link yet with delivery have shippingDate, but dont have rescheduleDate
    @EntityGraph(attributePaths = {"addressExternal"})
    @Query("SELECT o FROM OrderExternal o " +
            "WHERE o.shippingDate IS NOT NULL " +
            "  AND o.rescheduleDate IS NULL " +
            "  AND o.delivery IS NULL")
    List<OrderExternal> findScheduledOrders(Sort sort);

    @EntityGraph(attributePaths = {"addressExternal"})
    @Query("SELECT o FROM OrderExternal o " +
            "WHERE o.delivery.id = :deliveryId " +
            "  AND o.deliveryType = 'REGULAR' " +
            "  AND o.excluded = false " +
            "  AND o.addressExternal.status = 'UNKNOWN'")
    List<OrderExternal> findIncludedRegularOrdersWithUnknownAddressByDeliveryId(@Param("deliveryId") UUID deliveryId);

    @EntityGraph(attributePaths = {"addressExternal"})
    @Query("SELECT o FROM OrderExternal o " +
            "WHERE o.delivery.id = :deliveryId " +
            "  AND o.deliveryType = 'REGULAR' " +
            "  AND o.excluded = false ")
    List<OrderExternal> findOrdersForCalculation(@Param("deliveryId") UUID deliveryId);

    // Reschedule date is present only for outdated orders so should be excluded
    @EntityGraph(attributePaths = {"addressExternal"})
    @Query("SELECT o FROM OrderExternal o " +
            "WHERE o.shippingDate = :shippingDate" +
            "  AND o.rescheduleDate IS NULL " +
            "  AND o.delivery IS NULL")
    List<OrderExternal> findScheduledOrdersByShippingDate(@Param("shippingDate") LocalDate shippingDate);

    @EntityGraph(attributePaths = {"addressExternal", "delivery"})
    @Query("SELECT o FROM OrderExternal o WHERE o.id IN :orderIds")
    List<OrderExternal> findByOrderIds(@Param("orderIds") List<Long> orderIds);

    @Override
    @EntityGraph(attributePaths = {"addressExternal", "delivery"})
    @Query("SELECT o FROM OrderExternal o WHERE o.id = :id")
    Optional<OrderExternal> findById(@Param("id") Long id);
}
