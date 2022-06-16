package com.goods.partner.service.impl;

import com.goods.partner.dto.CalculationDto;
import com.goods.partner.dto.ClientDto;
import com.goods.partner.dto.OrderDto;
import com.goods.partner.dto.StoreDto;
import com.goods.partner.entity.Client;
import com.goods.partner.entity.Order;
import com.goods.partner.entity.projection.StoreProjection;
import com.goods.partner.mapper.ClientMapper;
import com.goods.partner.mapper.OrderMapper;
import com.goods.partner.mapper.StoreMapper;
import com.goods.partner.repository.ClientRepository;
import com.goods.partner.repository.OrderRepository;
import com.goods.partner.repository.StoreRepository;
import com.goods.partner.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final ClientRepository clientRepository;
    private final StoreRepository storeRepository;
    private final OrderMapper orderMapper;
    private final ClientMapper clientMapper;
    private final StoreMapper storeMapper;

    @Transactional
    public CalculationDto calculate(LocalDate date) {

        List<Order> ordersByDate = orderRepository.findAllByShippingDateEquals(date);
        List<OrderDto> orderDtos = orderMapper.mapOrders(ordersByDate);

        Set<Client> clients = clientRepository.findClientsByOrderDate(date);
        List<ClientDto> clientDtos = clientMapper.mapClients(clients);

        List<StoreProjection> storeProjections = storeRepository.groupStoresByOrders(date);
        List<StoreDto> storeDtos = storeMapper.mapStoreGroup(storeProjections);

        CalculationDto calculationDto = new CalculationDto();
        calculationDto.setDate(date);
        calculationDto.setOrders(orderDtos);
        calculationDto.setClients(clientDtos);
        calculationDto.setStores(storeDtos);
        return calculationDto;
    }

}
