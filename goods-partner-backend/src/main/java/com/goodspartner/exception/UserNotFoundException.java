package com.goodspartner.exception;

public class UserNotFoundException extends RuntimeException {

    private static final String USER_NOT_FOUND_BY_EMAIL_MESSAGE = "User not found with email: %s";
    private static final String USER_NOT_FOUND__BY_ID_MESSAGE = "User not found with id: %d";
    private static final String USER_NOT_FOUND_BY_ID_ROUTE_ID = "User not found for route ID: %s";

    private static final String USER_NOT_FOUND_BY_EMAIL_MESSAGE_UKR = "Користувача з email: %s не існує";
    private static final String USER_NOT_FOUND__BY_ID_MESSAGE_UKR = "Користувача з id: %d не існує";

    public UserNotFoundException(String email) {
        super(String.format(USER_NOT_FOUND_BY_EMAIL_MESSAGE_UKR, email));
    }

    public UserNotFoundException(int id) {
        super(String.format(USER_NOT_FOUND__BY_ID_MESSAGE_UKR, id));
    }

}
