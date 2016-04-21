package com.myMoneyTracker.controller.exception;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Created by floryn on 06.03.2016.
 */
@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class NotFoundException extends RuntimeException {

    private static final long serialVersionUID = -2729572918553774416L;
    
    private static final Logger log = Logger.getLogger(NotFoundException.class.getName());

    public NotFoundException() {
        super();
    }

    public NotFoundException(String message, Throwable cause) {
        super(message, cause);
        log.log(Level.INFO, message);
    }

    public NotFoundException(String message) {
        super(message);
        log.log(Level.INFO, message);
    }

    public NotFoundException(Throwable cause) {
        super(cause);
        log.log(Level.INFO, cause.getMessage());
    }
}
