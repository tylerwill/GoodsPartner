package com.goodspartner.exception.delivery;

import java.time.LocalDate;

public class DeliveryAlreadyExistException extends RuntimeException {

    private static final String MESSAGE_UKR = "Вже існує доставка на дату: %s оберіть іншу дату";

    public DeliveryAlreadyExistException(LocalDate deliveryDate) {
        super(String.format(MESSAGE_UKR, deliveryDate));
    }
}
