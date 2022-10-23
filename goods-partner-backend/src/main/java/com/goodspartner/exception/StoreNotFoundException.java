package com.goodspartner.exception;

public class StoreNotFoundException extends RuntimeException {

    public StoreNotFoundException(String message) {
        super(message);
    }

}