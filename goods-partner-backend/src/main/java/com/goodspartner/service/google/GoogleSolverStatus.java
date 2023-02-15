package com.goodspartner.service.google;

import java.util.Arrays;

public enum GoogleSolverStatus {

    ROUTING_NOT_SOLVED(0), // Problem not solved yet.
    ROUTING_SUCCESS(1),
    ROUTING_FAIL(2), // No solution found to the problem.
    ROUTING_FAIL_TIMEOUT(3), // Time limit reached before finding a solution.
    ROUTING_INVALID(4) // Model, model parameters, or flags are not valid.
    ;

    private final int code;

    GoogleSolverStatus(int code) {
        this.code = code;
    }

    public static GoogleSolverStatus getByCode(int code) {
        return Arrays.stream(values())
                .filter(value -> value.code == code)
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Invalid code: " + code));

    }
}
