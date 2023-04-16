package com.goodspartner.service.impl;

import com.goodspartner.configuration.properties.ClientRoutingProperties;
import com.goodspartner.dto.AddressExternalDto;
import com.goodspartner.dto.MapPoint;
import com.goodspartner.dto.OrderDto;
import com.goodspartner.entity.AddressExternal;
import com.goodspartner.entity.AddressExternal.OrderAddressId;
import com.goodspartner.entity.DeliveryType;
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

    private final ClientRoutingProperties clientRoutingProperties;
    private final AddressExternalRepository repository;
    private final AddressExternalMapper mapper;

    private static final Sort DEFAULT_ADDRESS_EXTERNAL_SORT = Sort.by(Sort.Direction.DESC, "orderAddressId");

    @Override
    public List<AddressExternalDto> findAll() {
        return repository.findAll(DEFAULT_ADDRESS_EXTERNAL_SORT)
                .stream()
                .map(mapper::toAddressExternalDto)
                .map(this::remapDefaultServiceTime)
                .toList();
    }

    private AddressExternalDto remapDefaultServiceTime(AddressExternalDto addressExternalDto) {
        MapPoint mapPoint = addressExternalDto.getMapPoint();
        if (mapPoint.getServiceTimeMinutes() == 0) {
            mapPoint.setServiceTimeMinutes(clientRoutingProperties.getUnloadingTimeMinutes());
        }
        return addressExternalDto;
    }

    @Transactional
    @Override
    public AddressExternalDto update(AddressExternalDto addressExternalDto) {
        OrderAddressId id = mapOrderAddressId(addressExternalDto);
        AddressExternal addressExternal = repository.findById(id)
                .map(address -> mapper.update(address, addressExternalDto.getMapPoint()))
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
    public void saveFromOrders(List<OrderDto> orders) {
        // Address reconciliation
        Set<AddressExternal> addresses = orders
                .stream()
                .filter(orderExternal -> DeliveryType.REGULAR.equals(orderExternal.getDeliveryType()))
                .map(mapper::mapToAddressExternal)
                .map(this::eraseDefaultServiceTime)
                .collect(Collectors.toSet());
        repository.saveAll(addresses);
    }

    private AddressExternal eraseDefaultServiceTime(AddressExternal addressExternal) {
        Integer serviceTimeMinutes = addressExternal.getServiceTimeMinutes();
        if (serviceTimeMinutes == null || serviceTimeMinutes.equals(clientRoutingProperties.getUnloadingTimeMinutes())) {
            addressExternal.setServiceTimeMinutes(null); // Empty for default service time
        }
        return addressExternal;
    }

    private OrderAddressId mapOrderAddressId(AddressExternalDto addressExternalDto) {
        return OrderAddressId.builder()
                .orderAddress( addressExternalDto.getOrderAddress())
                .clientName( addressExternalDto.getClientName())
                .build();
    }
}
