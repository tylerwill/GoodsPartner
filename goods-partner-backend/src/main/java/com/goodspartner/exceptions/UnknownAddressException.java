package com.goodspartner.exceptions;

public class UnknownAddressException extends RuntimeException{

    public UnknownAddressException(String message) {
        super(message);
    }
}
