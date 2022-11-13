package com.goodspartner.repository;

import com.goodspartner.entity.Car;
import com.goodspartner.entity.Delivery;
import com.goodspartner.entity.OrderExternal;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface OrderExternalRepository extends JpaRepository<OrderExternal, Integer> {

    @EntityGraph(attributePaths = {"addressExternal"})
    @Query("SELECT o FROM OrderExternal o WHERE o.delivery = :delivery AND o.carLoad.car = :car")
    List<OrderExternal> findAllByDeliveryAndCar(@Param("delivery") Delivery delivery,
                                                @Param("car") Car car);

    @EntityGraph(attributePaths = {"addressExternal"})
    @Query("SELECT o FROM OrderExternal o " +
            "WHERE o.excluded = TRUE " +
            "   OR o.dropped = TRUE " +
            "   OR o.routePoint.status = 'SKIPPED'")
    List<OrderExternal> findSkippedOrders();

    @EntityGraph(attributePaths = {"addressExternal"})
    @Query("SELECT o FROM OrderExternal o " +
            "WHERE o.excluded = FALSE " +
            "   AND o.dropped = FALSE " +
            "   AND o.routePoint.status = 'DONE'")
    List<OrderExternal> findCompletedOrders();

    List<OrderExternal> findByRescheduleDate(LocalDate date);
}
