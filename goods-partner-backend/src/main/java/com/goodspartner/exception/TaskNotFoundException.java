package com.goodspartner.exception;

public class TaskNotFoundException extends RuntimeException {

    private static final String MESSAGE = "Task with id: %d was not found";

    public TaskNotFoundException(long taksId) {
        super(String.format(MESSAGE, taksId));
    }
}
