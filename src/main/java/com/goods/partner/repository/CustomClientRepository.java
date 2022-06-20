package com.goods.partner.repository;

import com.goods.partner.entity.Client;

import java.time.LocalDate;
import java.util.List;

public interface CustomClientRepository {
    List<Client> findClientsByOrderDate(LocalDate date);
}
