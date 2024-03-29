package com.goodspartner.exception;


import com.goodspartner.entity.User;

public class CarNotFoundException extends RuntimeException {

    private static final String NO_CAR_BY_ID_MESSAGE = "There is no car with id: %s";
    private static final String NO_CAR_BY_USER_MESSAGE = "There is no car assigned to user: %s";

    private static final String NO_CAR_BY_ID_MESSAGE_UKR = "Не існує автомобіля з id: %s";
    private static final String NO_CAR_BY_USER_MESSAGE_UKR = "Жоден з автомобілів не призначений водієві: %s";

    public CarNotFoundException(String message) {
        super(message);
    }

    public CarNotFoundException(int id) {
        super(String.format(NO_CAR_BY_ID_MESSAGE_UKR, id));
    }

    public CarNotFoundException(User user) {
        super(String.format(NO_CAR_BY_USER_MESSAGE_UKR, user.getUserName()));
    }

}