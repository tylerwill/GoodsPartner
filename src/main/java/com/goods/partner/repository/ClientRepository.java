package com.goods.partner.repository;

import com.goods.partner.entity.Client;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Set;

@Repository
public interface ClientRepository extends JpaRepository<Client, LocalDate> {

    @Query("SELECT a FROM Client a JOIN a.addresses ad JOIN ad.orders o WHERE o.date < :date")
    Set<Client> findClientsByOrderDate(LocalDate date);
}
