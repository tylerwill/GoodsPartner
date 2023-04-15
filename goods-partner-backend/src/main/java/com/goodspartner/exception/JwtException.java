package com.goodspartner.exception;

public class JwtException extends RuntimeException {

    public JwtException(Exception exception) {
        super(exception);
    }
}
