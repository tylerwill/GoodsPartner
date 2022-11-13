package com.goodspartner.service.util;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.function.Consumer;
import java.util.function.Function;

@Component
public class TxWrapper {

    @Transactional
    public <T> void runInTransaction(Consumer<T> consumer, T value) {
        consumer.accept(value);
    }

    @Transactional
    public <T,R> R runInTransaction(Function<T, R> function, T value) {
        return function.apply(value);
    }
}