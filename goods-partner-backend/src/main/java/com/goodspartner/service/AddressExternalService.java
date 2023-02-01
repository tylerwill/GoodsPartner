package com.goodspartner.service;

import com.goodspartner.entity.AddressExternal;

import java.util.List;

public interface AddressExternalService {
    List<AddressExternal> findAll();

    AddressExternal update(AddressExternal addressExternal);

    void delete(AddressExternal addressExternal);
}
