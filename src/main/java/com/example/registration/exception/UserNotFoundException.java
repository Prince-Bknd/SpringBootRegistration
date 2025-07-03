package com.example.registration.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class UserNotFoundException extends RuntimeException {
    
    public UserNotFoundException(String resourceName, String fieldName, Object fieldValue) {
        super(formatMessage(resourceName, fieldName, fieldValue));
        logException(); 
    }
    
    public UserNotFoundException(String message) {
        super(message);
    }

    private static String formatMessage(String resourceName, String fieldName, Object fieldValue) {
        return String.format("%s not found with %s: '%s'", 
            sanitize(resourceName), 
            sanitize(fieldName), 
            sanitize(fieldValue.toString()));
    }

    private static String sanitize(String input) {
        return input.replaceAll("[<>\"']", "");
    }

    private void logException() {
        System.getLogger(UserNotFoundException.class.getName())
            .log(System.Logger.Level.ERROR, "User not found: " + getMessage());
    }
}