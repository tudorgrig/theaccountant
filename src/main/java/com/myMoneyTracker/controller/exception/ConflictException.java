package com.myMoneyTracker.controller.exception;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Created by Floryn on 06.03.2016.
 */
@ResponseStatus(value = HttpStatus.CONFLICT)
public class ConflictException extends RuntimeException{
    
    private static final long serialVersionUID = 2274321605661987027L;
   
    private static final Logger log = Logger.getLogger(ConflictException.class.getName());

    public ConflictException() {
        super();
    }

    public ConflictException(String message, Throwable cause) {
        super(message, cause);
        log.log(Level.INFO, message);
    }

    public ConflictException(String message) {
        super(message);
        log.log(Level.INFO, message);
    }

    public ConflictException(Throwable cause) {
        super(cause);
        log.log(Level.INFO, cause.getMessage());
    }
}
