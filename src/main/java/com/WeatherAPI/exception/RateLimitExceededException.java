package com.WeatherAPI.exception;

public class RateLimitExceededException extends RuntimeException {
    public RateLimitExceededException() {
    }

    public RateLimitExceededException(String message) {
        super(message);
    }
}
