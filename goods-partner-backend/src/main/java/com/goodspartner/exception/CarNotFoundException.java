package com.goodspartner.exception;


public class CarNotFoundException extends RuntimeException {

    private static final String NO_CAR_BY_ID_MESSAGE = "There is no car with id: %s";

    public CarNotFoundException(String message) {
        super(message);
    }

    public CarNotFoundException(int id) {
        super(String.format(NO_CAR_BY_ID_MESSAGE, id));
    }

}