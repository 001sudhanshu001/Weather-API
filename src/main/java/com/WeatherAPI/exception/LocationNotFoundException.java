package com.WeatherAPI.exception;

public class LocationNotFoundException extends RuntimeException{
    public LocationNotFoundException(String message) {
        super(message);
    }

    public LocationNotFoundException(String countryCode, String cityName) {
        super(String.format("No Location found with the given country code : %s and city name : %s", countryCode, cityName));
    }
}
