package com.goods.partner.service.impl;

import com.goods.partner.dto.*;
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

    @Override
    @Transactional
    public CalculationOrdersDto calculateOrders(LocalDate date) {

        List<Order> ordersByDate = orderRepository.findAllByShippingDateEquals(date);
        List<OrderDto> orderDtos = orderMapper.mapOrders(ordersByDate);

        CalculationOrdersDto calculationOrdersDto = new CalculationOrdersDto();
        calculationOrdersDto.setDate(date);
        calculationOrdersDto.setOrders(orderDtos);
        return calculationOrdersDto;
    }

    @Override
    @Transactional
    public CalculationAddressesDto calculateAddresses(LocalDate date) {

        List<Client> clients = clientRepository.findClientsByOrderDate(date);
        List<ClientDto> clientDtos = clientMapper.mapClients(clients);

        CalculationAddressesDto calculationAddressesDto = new CalculationAddressesDto();
        calculationAddressesDto.setDate(date);
        calculationAddressesDto.setClients(clientDtos);

        return calculationAddressesDto;
    }

    @Override
    @Transactional
    public CalculationStoresDto calculateStores(LocalDate date) {

        List<StoreProjection> storeProjections = storeRepository.groupStoresByOrders(date);
        List<StoreDto> storeDtos = storeMapper.mapStoreGroup(storeProjections);

        CalculationStoresDto calculationStoresDto = new CalculationStoresDto();
        calculationStoresDto.setDate(date);
        calculationStoresDto.setStores(storeDtos);

        return calculationStoresDto;
    }
}