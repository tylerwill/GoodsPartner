package com.goodspartner.service.impl;

import com.goodspartner.dto.OrderDto;
import com.goodspartner.entity.OrderExternal;
import com.goodspartner.mapper.OrderExternalMapper;
import com.goodspartner.repository.OrderExternalRepository;
import com.goodspartner.service.OrderExternalService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Slf4j
@Service
public class DefaultOrderExternalService implements OrderExternalService {

    private final OrderExternalMapper orderExternalMapper;

    private final OrderExternalRepository orderExternalRepository;

    @Override
    public void save(Map<Boolean, List<OrderDto>> sortedOrderDtos) {
        List<OrderDto> sortedOrderDto = sortedOrderDtos.values().stream()
                .flatMap(Collection::stream)
                .collect(Collectors.toList());

        List<OrderExternal> sortedOrders = orderExternalMapper.mapOrderDtosToOrderExternal(sortedOrderDto);

        orderExternalRepository.saveAll(sortedOrders);
    }
}