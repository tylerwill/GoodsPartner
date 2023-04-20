package com.goodspartner.exception;

public class UserNotFoundException extends RuntimeException {

    private static final String USER_NOT_FOUND_BY_LOGIN_MESSAGE = "User not found with login: %s";
    private static final String USER_NOT_FOUND__BY_ID_MESSAGE = "User not found with id: %d";
    private static final String USER_NOT_FOUND_BY_ID_ROUTE_ID = "User not found for route ID: %s";

    private static final String USER_NOT_FOUND_BY_EMAIL_LOGIN_UKR = "Користувача з login: %s не існує";
    private static final String USER_NOT_FOUND_BY_ID_MESSAGE_UKR = "Користувача з id: %d не існує";

    public UserNotFoundException(String login) {
        super(String.format(USER_NOT_FOUND_BY_EMAIL_LOGIN_UKR, login));
    }

    public UserNotFoundException(int id) {
        super(String.format(USER_NOT_FOUND_BY_ID_MESSAGE_UKR, id));
    }

}
