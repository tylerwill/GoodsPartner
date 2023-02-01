package com.goodspartner.service.impl;

import com.goodspartner.entity.AddressExternal;
import com.goodspartner.exception.AddressExternalNotFoundException;
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

    @Transactional(readOnly = true)
    @Override
    public List<AddressExternal> findAll() {
        return repository.findAll();
    }

    @Transactional
    @Override
    public AddressExternal update(AddressExternal addressExternal) {
        isPresent(addressExternal);
        return repository.save(addressExternal);
    }

    @Transactional
    @Override
    public void delete(AddressExternal addressExternal) {
        isPresent(addressExternal);
        repository.delete(addressExternal);
    }

    private void isPresent(AddressExternal addressExternal) {
        AddressExternal.OrderAddressId id = addressExternal.getOrderAddressId();
        repository.findById(id).orElseThrow(() -> new AddressExternalNotFoundException(id));
    }
}
