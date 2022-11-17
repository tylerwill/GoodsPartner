package com.goodspartner.service;

import com.goodspartner.dto.ProductShippingDto;

import java.util.List;
import java.util.UUID;

public interface ShippingService {

    List<ProductShippingDto> findByDeliveryId(UUID id);
}
