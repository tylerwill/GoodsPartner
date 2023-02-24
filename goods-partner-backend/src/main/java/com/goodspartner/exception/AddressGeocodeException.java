package com.goodspartner.exception;


public class AddressGeocodeException extends RuntimeException {
    private static final String MESSAGE = "Exception while trying to geocode address: %s";
    private static final String MESSAGE_UKR = "Помилка під час геолокації адреси: %s";

    public AddressGeocodeException(String address, Throwable cause) {
        super(String.format(MESSAGE_UKR, address), cause);
    }

    public AddressGeocodeException() {
    }
}