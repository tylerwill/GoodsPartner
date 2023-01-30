package com.goodspartner.service.impl;

import com.goodspartner.dto.AddressExternalDto;
import com.goodspartner.entity.AddressExternal;
import com.goodspartner.exception.AddressExternalNotFoundException;
import com.goodspartner.mapper.AddressExternalMapper;
import com.goodspartner.repository.AddressExternalRepository;
import com.goodspartner.service.AddressExternalService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Service
public class DefaultAddressExternalService implements AddressExternalService {

    private final AddressExternalRepository repository;
    private final AddressExternalMapper mapper;

    @Transactional(readOnly = true)
    @Override
    public List<AddressExternalDto> findAll() {
        List<AddressExternal> addressExternalList = repository.findAll();

        return addressExternalList.stream()
                .map(mapper::toAddressExternalDto)
                .toList();
    }

    @Transactional
    @Override
    public AddressExternalDto update(AddressExternalDto addressExternalDto) {
        AddressExternal convertedAddressExternal = mapper.toAddressExternal(addressExternalDto);

        isPresent(convertedAddressExternal);

        return mapper.toAddressExternalDto(
                repository.save(convertedAddressExternal));
    }

    @Transactional
    @Override
    public void delete(AddressExternalDto addressExternalDto) {
        AddressExternal convertedAddressExternal = mapper.toAddressExternal(addressExternalDto);

        isPresent(convertedAddressExternal);

        repository.delete(convertedAddressExternal);
    }

    private void isPresent(AddressExternal convertedAddressExternal) {
        AddressExternal.OrderAddressId id = convertedAddressExternal.getOrderAddressId();
        repository
                .findById(id).orElseThrow(() -> new AddressExternalNotFoundException(id));
    }
}
