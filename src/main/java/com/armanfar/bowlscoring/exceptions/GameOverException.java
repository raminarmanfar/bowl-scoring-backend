package com.armanfar.bowlscoring.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class GameOverException extends BowlScoringException {
    public GameOverException(String message) {
        super(message);
    }
}
