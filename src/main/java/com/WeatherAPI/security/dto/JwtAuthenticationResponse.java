package com.WeatherAPI.security.dto;

import com.WeatherAPI.entity.UserSession;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JwtAuthenticationResponse {
    private String accessToken;
    private String refreshToken;

    @JsonIgnore
    private List<UserSession> loggedOutSessions = new ArrayList<>();

    @JsonIgnore
    private String userName;
}
