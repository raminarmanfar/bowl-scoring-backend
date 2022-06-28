package com.armanfar.bowlscoring.exceptions;

public class BowlScoringException extends RuntimeException {
    @Override
    public synchronized Throwable fillInStackTrace() {
        return this;
    }
    public BowlScoringException(String message) {
        super(message);
    }
}
