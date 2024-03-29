package com.goodspartner.repository;

import com.goodspartner.entity.Car;
import com.goodspartner.entity.DeliveryType;
import com.goodspartner.entity.OrderExternal;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface OrderExternalRepository extends JpaRepository<OrderExternal, Long> {

    @Query("SELECT o FROM OrderExternal o WHERE o.delivery.id = :deliveryId")
    List<OrderExternal> findByDeliveryId(@Param("deliveryId") UUID deliveryId, Sort sort);

    @Query("SELECT o FROM OrderExternal o WHERE o.delivery.id = :deliveryId AND o.carLoad.car = :car")
    List<OrderExternal> findAllByDeliveryAndCar(@Param("deliveryId") UUID deliveryId,
                                                @Param("car") Car car, Sort sort);

    // For skipped order we do not set rescheduleDate yet
    @Query("SELECT o FROM OrderExternal o LEFT JOIN o.routePoint rp " +
            "WHERE o.rescheduleDate IS NULL " +
            "  AND (o.excluded = TRUE OR o.dropped = TRUE OR rp.status = 'SKIPPED')")
    List<OrderExternal> findSkippedOrders(Sort sort);

    @Query("SELECT o FROM OrderExternal o " +
            "WHERE o.excluded = FALSE " +
            "  AND o.dropped = FALSE " +
            "  AND o.routePoint.status = 'DONE'")
    List<OrderExternal> findCompletedOrders(Sort sort);

    // Scheduled orders doesn't link yet with delivery have shippingDate, but dont have rescheduleDate
    @Query("SELECT o FROM OrderExternal o " +
            "WHERE o.shippingDate IS NOT NULL " +
            "  AND o.rescheduleDate IS NULL " +
            "  AND o.delivery IS NULL")
    List<OrderExternal> findScheduledOrders(Sort sort);

    @Query("SELECT o FROM OrderExternal o " +
            "WHERE o.delivery.id = :deliveryId " +
            "AND o.deliveryType = :deliveryType " +
            "AND o.excluded = false")
    List<OrderExternal> findOrdersByDeliveryIdAndDeliveryType(@Param("deliveryId") UUID deliveryId, @Param("deliveryType") DeliveryType deliveryType);

    @EntityGraph(attributePaths = {"delivery"})
    @Query("SELECT o FROM OrderExternal o " +
            "WHERE o.delivery.id = :deliveryId " +
            "  AND o.deliveryType = 'REGULAR' " +
            "  AND o.excluded = false ")
    List<OrderExternal> findOrdersForCalculation(@Param("deliveryId") UUID deliveryId);

    // Reschedule date is present only for outdated orders so should be excluded
    @Query("SELECT o FROM OrderExternal o " +
            "WHERE o.shippingDate = :shippingDate" +
            "  AND o.rescheduleDate IS NULL " +
            "  AND o.delivery IS NULL")
    List<OrderExternal> findScheduledOrdersByShippingDate(@Param("shippingDate") LocalDate shippingDate);

    @EntityGraph(attributePaths = {"delivery"})
    @Query("SELECT o FROM OrderExternal o WHERE o.id IN :orderIds")
    List<OrderExternal> findByOrderIds(@Param("orderIds") List<Long> orderIds);

    @EntityGraph(attributePaths = {"delivery"})
    @Query("SELECT o FROM OrderExternal o WHERE o.id = :id")
    Optional<OrderExternal> findByIdWithDelivery(@Param("id") Long id);

    @Modifying
    @Query("DELETE FROM OrderExternal WHERE id IN " +
            "(SELECT go.id FROM OrderExternal go " +
            "        LEFT JOIN OrderExternal resc " +
            "            ON (go.orderNumber = resc.orderNumber AND go.shippingDate = resc.rescheduleDate) " +
            "  WHERE go.delivery.id = :id AND resc.id is null)")
    void removeCRMOrdersByDeliveryId(@Param("id") UUID id);

}
