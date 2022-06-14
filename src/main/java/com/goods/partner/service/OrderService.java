package com.goods.partner.service;

import com.goods.partner.entity.Order;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class OrderService {
    public List<Order> getByDate(LocalDate date) {
        return null;
    }
}
