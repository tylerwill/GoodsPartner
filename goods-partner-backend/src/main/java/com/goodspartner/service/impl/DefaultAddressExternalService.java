package com.goodspartner.service.impl;

import com.goodspartner.dto.AddressExternalDto;
import com.goodspartner.entity.AddressExternal;
import com.goodspartner.entity.AddressExternal.OrderAddressId;
import com.goodspartner.entity.DeliveryType;
import com.goodspartner.entity.OrderExternal;
import com.goodspartner.exception.AddressExternalNotFoundException;
import com.goodspartner.mapper.AddressExternalMapper;
import com.goodspartner.repository.AddressExternalRepository;
import com.goodspartner.service.AddressExternalService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class DefaultAddressExternalService implements AddressExternalService {

    private final AddressExternalRepository repository;
    private final AddressExternalMapper mapper;

    private static final Sort DEFAULT_ADDRESS_EXTERNAL_SORT = Sort.by(Sort.Direction.DESC, "orderAddressId");

    @Override
    public List<AddressExternalDto> findAll() {
        return repository.findAll(DEFAULT_ADDRESS_EXTERNAL_SORT)
                .stream()
                .map(mapper::toAddressExternalDto)
                .toList();
    }

    @Transactional
    @Override
    public AddressExternalDto update(AddressExternalDto addressExternalDto) {
        OrderAddressId id = mapOrderAddressId(addressExternalDto);
        AddressExternal addressExternal = repository.findById(id)
                .map(address -> mapper.update(address, addressExternalDto))
                .orElseThrow(() -> new AddressExternalNotFoundException(id));
        return mapper.toAddressExternalDto(addressExternal);
    }

    @Transactional
    @Override
    public void delete(AddressExternalDto addressExternalDto) {
        OrderAddressId id = mapOrderAddressId(addressExternalDto);
        AddressExternal addressExternal = repository.findById(id)
                .orElseThrow(() -> new AddressExternalNotFoundException(id));
        repository.deleteById(addressExternal.getOrderAddressId());
    }

    // Do not save address for orders which are shipped by other delivery types then REGULAR
    @Transactional
    @Override
    public void saveFromOrders(List<OrderExternal> orders) {
        // Address reconciliation
        Set<AddressExternal> addresses = orders
                .stream()
                .filter(orderExternal -> DeliveryType.REGULAR.equals(orderExternal.getDeliveryType()))
                .map(mapper::mapToAddressExternal)
                .collect(Collectors.toSet());
        repository.saveAll(addresses);
    }

    private OrderAddressId mapOrderAddressId(AddressExternalDto addressExternalDto) {
        return OrderAddressId.builder()
                .orderAddress( addressExternalDto.getOrderAddress())
                .clientName( addressExternalDto.getClientName())
                .build();
    }
}
