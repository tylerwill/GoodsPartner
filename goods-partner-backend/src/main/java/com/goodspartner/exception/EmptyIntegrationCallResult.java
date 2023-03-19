package com.goodspartner.exception;

public class EmptyIntegrationCallResult extends RuntimeException {

    private static final String MESSAGE = "Помалка інтеграції із CRM системою. Отримано пустий результат";

    public EmptyIntegrationCallResult() {
        super(MESSAGE);
    }
}
