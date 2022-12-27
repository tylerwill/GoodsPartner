package com.goodspartner.service.util;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

@Component
public class TxWrapper {

    @Transactional(readOnly = true, propagation = Propagation.REQUIRES_NEW)
    public <T> void runNewReadOnlyTransaction(Supplier<T> supplier) {
        supplier.get();
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public <T> T runNewTransaction(Supplier<T> supplier) {
        return supplier.get();
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public <T> void runNewTransaction(Consumer<T> consumer, T value) {
        consumer.accept(value);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public <T,R> R runNewTransaction(Function<T, R> function, T value) {
        return function.apply(value);
    }
}