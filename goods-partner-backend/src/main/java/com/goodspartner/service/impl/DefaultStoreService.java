package com.goodspartner.service.impl;

import com.goodspartner.entity.Store;
import com.goodspartner.exceptions.StoreNotFoundException;
import com.goodspartner.repository.StoreRepository;
import com.goodspartner.service.StoreService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class DefaultStoreService implements StoreService {

    private static final String STORE_NAME = "Склад №1";

    @Autowired
    private final StoreRepository storeRepository;

    @Override
    @Transactional(readOnly = true)
    public Store getMainStore() {
        return storeRepository.findByName(STORE_NAME)
                .orElseThrow(() -> new StoreNotFoundException(STORE_NAME));
    }
}
