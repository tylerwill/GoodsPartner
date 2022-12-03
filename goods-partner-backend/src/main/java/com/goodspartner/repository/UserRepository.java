package com.goodspartner.repository;

import com.goodspartner.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {

    Optional<User> findUserByEmail(String email);

    @Query(value = "SELECT u FROM Route r JOIN r.car c JOIN c.driver u WHERE r.id = :id")
    Optional<User> findByRouteId(@Param("id") long routeId);

}