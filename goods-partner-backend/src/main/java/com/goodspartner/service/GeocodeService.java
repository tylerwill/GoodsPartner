package com.goodspartner.service;

import com.goodspartner.dto.OrderDto;

import java.util.List;

public interface GeocodeService {

    void enrichValidAddress(List<OrderDto> orders);

}