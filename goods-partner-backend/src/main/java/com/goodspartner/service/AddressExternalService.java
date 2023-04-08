package com.goodspartner.service;

import com.goodspartner.dto.AddressExternalDto;
import com.goodspartner.entity.OrderExternal;

import java.util.List;

public interface AddressExternalService {
    List<AddressExternalDto> findAll();

    AddressExternalDto update(AddressExternalDto addressExternalDto);

    void delete(AddressExternalDto addressExternalDto);

    void saveFromOrders(List<OrderExternal> orders);
}
