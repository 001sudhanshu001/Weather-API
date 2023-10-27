package com.WeatherAPI.exception;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Getter @Setter
public class ErrorDTO {
    private Date timestamp;
    private int status;
    private String path;
    List<String> errors = new ArrayList<>();

    public void addError(String message){
        this.errors.add(message);
    }
}
