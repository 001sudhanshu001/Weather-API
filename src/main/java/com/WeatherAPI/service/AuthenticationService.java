package com.WeatherAPI.service;

import com.WeatherAPI.dao.UserRepository;
import com.WeatherAPI.dao.UserSessionDetailRepository;
import com.WeatherAPI.entity.AppUser;
import com.WeatherAPI.entity.UserSession;
import com.WeatherAPI.security.dto.JwtAuthenticationResponse;
import com.WeatherAPI.security.dto.SignUpRequest;
import com.WeatherAPI.security.dto.UserDetailsImpl;
import com.WeatherAPI.security.enums.Role;
import com.WeatherAPI.security.enums.TokenType;
import com.github.f4b6a3.ulid.Ulid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.Map;

@Service
@Transactional
@RequiredArgsConstructor
public class AuthenticationService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final UserSessionDetailRepository userSessionDetailRepository;

    @Transactional
    public JwtAuthenticationResponse signup(SignUpRequest signUpRequest) {

        AppUser appUser = AppUser.builder()
                .firstName(signUpRequest.getFirstName())
                .lastName(signUpRequest.getLastName())
                .email(signUpRequest.getEmail())
                .password(passwordEncoder.encode(signUpRequest.getPassword()))
                .role(Role.USER)
                .build();
        userRepository.save(appUser);

        Map<TokenType, String> tokenMappedByType = jwtService.generateBothToken(new UserDetailsImpl(appUser));

        saveLoginSession(tokenMappedByType, appUser);

        return JwtAuthenticationResponse.builder()
                .accessToken(tokenMappedByType.get(TokenType.ACCESS_TOKEN))
                .refreshToken(tokenMappedByType.get(TokenType.REFRESH_TOKEN))
                .build();
    }

    private void saveLoginSession(Map<TokenType, String> tokenMappedByType, AppUser user) {
        String accessToken = tokenMappedByType.get(TokenType.ACCESS_TOKEN);
        String refreshToken = tokenMappedByType.get(TokenType.REFRESH_TOKEN);

        Date refreshTokenWillExpireAt = new Date(Ulid.from(refreshToken).getTime());

        UserSession sessionDetail = new UserSession();

        sessionDetail.setActiveRefreshToken(refreshToken);
        sessionDetail.setAppUser(user);
        sessionDetail.setRefreshTokenExpiryDate(refreshTokenWillExpireAt);
        sessionDetail.setActiveAccessToken(accessToken);

        Date date = new Date();
        sessionDetail.setCreatedDate(date);
        sessionDetail.setLastModifiedDate(date);

        userSessionDetailRepository.save(sessionDetail);
    }
}
