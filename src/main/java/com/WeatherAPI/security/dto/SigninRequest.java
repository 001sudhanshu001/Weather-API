package com.WeatherAPI.security.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class SigninRequest {

    @NotBlank
    private String userName;

    @NotBlank
    private String password;
}