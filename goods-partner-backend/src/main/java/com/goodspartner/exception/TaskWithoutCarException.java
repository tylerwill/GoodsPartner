package com.goodspartner.exception;

public class TaskWithoutCarException extends RuntimeException{

    private static final String MESSAGE = "Unable to create task without specified car";

    public TaskWithoutCarException() {
        super(MESSAGE);
    }
}
