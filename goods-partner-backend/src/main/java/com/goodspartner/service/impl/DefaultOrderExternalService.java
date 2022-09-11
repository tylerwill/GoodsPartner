package com.goodspartner.service.impl;

import com.goodspartner.dto.OrderDto;
import com.goodspartner.entity.OrderExternal;
import com.goodspartner.mapper.OrderExternalMapper;
import com.goodspartner.repository.OrderExternalRepository;
import com.goodspartner.service.OrderExternalService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Slf4j
@Service
public class DefaultOrderExternalService implements OrderExternalService {

    private final OrderExternalMapper orderExternalMapper;
    private final OrderExternalRepository orderExternalRepository;

    @Override
    public void save(List<OrderDto> orders) {

        List<OrderExternal> externalOrders = orderExternalMapper.mapOrderDtosToOrderExternal(orders);

        orderExternalRepository.saveAll(externalOrders);
    }
}