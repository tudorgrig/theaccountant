package com.myMoneyTracker.controller.exception;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Created by Floryn on 06.03.2016
 */
@ResponseStatus(value = HttpStatus.CONFLICT)
public class BadRequestException extends RuntimeException {

    private static final long serialVersionUID = 7515599836542442450L;

    private static final Logger log = Logger.getLogger(BadRequestException.class.getName());

    public BadRequestException() {
        super();
    }

    public BadRequestException(String message, Throwable cause) {
        super(message, cause);
        log.log(Level.INFO, message);
    }

    public BadRequestException(String message) {
        super(message);
        log.log(Level.INFO, message);
    }

    public BadRequestException(Throwable cause) {
        super(cause);
        log.log(Level.INFO, cause.getMessage());
    }
}
