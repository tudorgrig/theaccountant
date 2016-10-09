package com.myMoneyTracker.controller.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Exception thrown in case of an unauthorized attempt
 * 
 * @author Florin
 */
@ResponseStatus(value = HttpStatus.UNAUTHORIZED)
public class UnauthorizedException extends RuntimeException {

    private static final long serialVersionUID = 873149534063872895L;
    
    private static final Logger log = Logger.getLogger(UnauthorizedException.class.getName());

    public UnauthorizedException() {
        super();
    }

    public UnauthorizedException(String message, Throwable cause) {
        super(message, cause);
        log.log(Level.INFO, message);
    }

    public UnauthorizedException(String message) {
        super(message);
        log.log(Level.INFO, message);
    }

    public UnauthorizedException(Throwable cause) {
        super(cause);
        log.log(Level.INFO, cause.getMessage());
    }
}
