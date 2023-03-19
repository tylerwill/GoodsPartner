package com.goodspartner.service;

import com.goodspartner.dto.AddressExternalDto;

import java.util.List;

public interface AddressExternalService {
    List<AddressExternalDto> findAll();

    AddressExternalDto update(AddressExternalDto addressExternalDto);

    void delete(AddressExternalDto addressExternalDto);
}
