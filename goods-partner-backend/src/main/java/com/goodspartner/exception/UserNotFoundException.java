package com.goodspartner.exception;

public class UserNotFoundException extends RuntimeException {

    private static final String USER_NOT_FOUND_BY_EMAIL_MESSAGE = "User not found with email: %s";
    private static final String USER_NOT_FOUND__BY_ID_MESSAGE = "User not found with email: %d";

    public UserNotFoundException(String email) {
        super(String.format(USER_NOT_FOUND_BY_EMAIL_MESSAGE, email));
    }

    public UserNotFoundException(int id) {
        super(String.format(USER_NOT_FOUND__BY_ID_MESSAGE, id));
    }
}
