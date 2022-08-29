package com.goodspartner.service.impl;

import com.goodspartner.dto.OrderDto;
import com.goodspartner.entity.OrderExternal;
import com.goodspartner.mapper.OrderExternalMapper;
import com.goodspartner.repository.OrderExternalRepository;
import com.goodspartner.service.OrderExternalService;
import com.goodspartner.service.dto.OrderValidationDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.ListUtils;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Slf4j
@Service
public class DefaultOrderExternalService implements OrderExternalService {

    private final OrderExternalMapper orderExternalMapper;

    private final OrderExternalRepository orderExternalRepository;

    // Not used so far. Stub for a future DeliveryService
    @Override
    public void save(OrderValidationDto orderValidationDto) {

        List<OrderDto> validOrders = orderValidationDto.getValidOrders();
        List<OrderDto> invalidOrders = orderValidationDto.getInvalidOrders();

        List<OrderDto> allOrders = ListUtils.union(validOrders, invalidOrders);

        List<OrderExternal> externalOrders = orderExternalMapper.mapOrderDtosToOrderExternal(allOrders);

        orderExternalRepository.saveAll(externalOrders);
    }
}