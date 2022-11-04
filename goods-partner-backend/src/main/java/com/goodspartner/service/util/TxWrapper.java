package com.goodspartner.service.util;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.function.Consumer;

@Component
public class TxWrapper {

    @Transactional
    public <T> void runInTransaction(Consumer<T> consumer, T value) {
        consumer.accept(value);
    }
}