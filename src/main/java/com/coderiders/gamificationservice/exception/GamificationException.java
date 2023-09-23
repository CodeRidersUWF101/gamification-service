package com.coderiders.gamificationservice.exception;

public class GamificationException extends RuntimeException {
    public GamificationException() {
        super();
    }

    public GamificationException(String message) {
        super(message);
    }

    public GamificationException(Throwable cause) {
        super(cause);
    }

    public GamificationException(String message, Throwable cause) {
        super(message, cause);
    }
}
