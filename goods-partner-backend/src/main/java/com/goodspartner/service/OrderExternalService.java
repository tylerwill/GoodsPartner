package com.goodspartner.service;

import com.goodspartner.dto.OrderDto;

import java.util.List;
import java.util.UUID;

public interface OrderExternalService {

    void saveValidOrdersAndEnrichKnownAddressesCache(UUID id, List<OrderDto> orderDtos);

}