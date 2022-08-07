package com.goodspartner.repository;

import com.goodspartner.entity.Client;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;

@Repository
public interface ClientRepository extends JpaRepository<Client, LocalDate>, CustomClientRepository {
}
