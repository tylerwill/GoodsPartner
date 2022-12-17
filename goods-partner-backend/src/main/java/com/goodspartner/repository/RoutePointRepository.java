package com.goodspartner.repository;

import com.goodspartner.entity.RoutePoint;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface RoutePointRepository extends JpaRepository<RoutePoint, Long> {

    @EntityGraph(attributePaths = {"addressExternal"})
    @Query(value = "SELECT rp FROM RoutePoint rp WHERE rp.route.id = :id")
    List<RoutePoint> findByRouteId(@Param("id") Long routeId);

    @EntityGraph(attributePaths = {"addressExternal"})
    @Query(value = "SELECT rp FROM RoutePoint rp WHERE rp.id = :id")
    Optional<RoutePoint> findByRoutePointId(@Param("id") Long routePointId);

    @EntityGraph(attributePaths = {"orders", "addressExternal"})
    @Query(value = "SELECT rp FROM RoutePoint rp WHERE rp.id = :id")
    Optional<RoutePoint> findByIdWithOrders(@Param("id") Long routePointId);
}
