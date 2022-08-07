package com.goodspartner.repository;

import com.goodspartner.entity.Client;

import java.time.LocalDate;
import java.util.List;

public interface CustomClientRepository {
    List<Client> findClientsByOrderDate(LocalDate date);
}
