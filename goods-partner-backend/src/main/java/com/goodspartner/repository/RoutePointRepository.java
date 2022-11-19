package com.goodspartner.repository;

import com.goodspartner.entity.RoutePoint;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface RoutePointRepository extends JpaRepository<RoutePoint, Long> {

    @EntityGraph(attributePaths = {"orders.addressExternal"})
    @Query(value = "SELECT rp FROM RoutePoint rp WHERE rp.route.id = :id")
    List<RoutePoint> findByRouteId(@Param("id") int routeId);

    @EntityGraph(attributePaths = {"orders.addressExternal"})
    @Query(value = "SELECT rp FROM RoutePoint rp WHERE rp.route.id in :ids")
    List<RoutePoint> findByMultipleRouteId(@Param("ids") List<Integer> routeIds);
}
