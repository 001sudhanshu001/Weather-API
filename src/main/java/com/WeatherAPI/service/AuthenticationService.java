package com.WeatherAPI.service;

import com.WeatherAPI.dao.UserRepository;
import com.WeatherAPI.dao.UserSessionDetailRepository;
import com.WeatherAPI.entity.AppUser;
import com.WeatherAPI.entity.UserSession;
import com.WeatherAPI.security.dto.*;
import com.WeatherAPI.security.enums.Role;
import com.WeatherAPI.security.enums.TokenType;
import com.WeatherAPI.security.exception.JwtSecurityException;
import com.WeatherAPI.security.helper.SessionCreationHelper;
import com.WeatherAPI.security.jwt.JwtTokenProperty;
import com.github.f4b6a3.ulid.Ulid;
import com.github.f4b6a3.ulid.UlidCreator;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

import static com.WeatherAPI.security.exception.JwtSecurityException.JWTErrorCode.REFRESH_TOKEN_ONLY_ALLOWED_WITH_EXPIRED_TOKEN;

@Service
@Transactional
@RequiredArgsConstructor
public class AuthenticationService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final UserSessionDetailRepository userSessionDetailRepository;
    private final SessionCreationHelper sessionCreationHelper;
    private final AuthenticationManager authenticationManager;

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

    @Transactional
    public JwtAuthenticationResponse signin(SigninRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUserName(), request.getPassword())
        );

        AppUser user = userRepository.findByEmail(request.getUserName())
                .orElseThrow(() -> new JwtSecurityException(
                        JwtSecurityException.JWTErrorCode.USER_NOT_FOUND,
                        "Invalid email or password")
                );

        List<UserSession> logoutSessions = validateAndReturnLogoutSession(user);

        Map<TokenType, String> tokenMappedByType = jwtService.generateBothToken(new UserDetailsImpl(user));
        saveLoginSession(tokenMappedByType, user);

        return JwtAuthenticationResponse.builder()
                .accessToken(tokenMappedByType.get(TokenType.ACCESS_TOKEN))
                .refreshToken(tokenMappedByType.get(TokenType.REFRESH_TOKEN))
                .loggedOutSessions(logoutSessions) // This is added to Trigger logout session in Controller
                .userName(request.getUserName())
                .build();
    }

    private List<UserSession> validateAndReturnLogoutSession(AppUser user) {
        List<UserSession> sessions = user.getUserSessions();
        if (sessions.isEmpty()) return List.of();

        int numberOfSessionsInDB = sessions.size();

        // methods calling order matters here
        removeInActiveSessionFromDB(sessions);
        throwExceptionIfNewSessionNotAllowed(numberOfSessionsInDB);
        return removeSessionIfAllowed(numberOfSessionsInDB, sessions);
    }

    private void removeInActiveSessionFromDB(List<UserSession> sessions) {
        List<UserSession> inActiveUserSessions = sessions.stream()
                .filter(UserSession::hasRefreshDateCrossed)
                .filter(this::isAccessTokenExpired)
                .toList();

        if (inActiveUserSessions.isEmpty()) {
            return;
        }

        // Delete InActive Sessions From Database
        userSessionDetailRepository.deleteAll(inActiveUserSessions);

        // Remove From Session List
        sessions.removeAll(inActiveUserSessions);
    }

    private boolean isAccessTokenExpired(UserSession session) {
        String accessToken = session.getActiveAccessToken();
        Date tokenExpiryDate = jwtService.getTokenExpiryFromExpiredJWT(accessToken);
        return tokenExpiryDate.before(new Date());
    }

    private void throwExceptionIfNewSessionNotAllowed(int numberOfSessionsInDB) {
        if (!sessionCreationHelper.canCreateNewSession(numberOfSessionsInDB)) {
            throw new JwtSecurityException(
                    JwtSecurityException.JWTErrorCode.MAX_SESSION_REACHED,
                    "Session Not Allowed, You Have To Logout From Other Device First"
            );
        }
    }

    private List<UserSession> removeSessionIfAllowed(
            int numberOfSessionsInDB, List<UserSession> sessions) {
        Integer allowedSessionCount = JwtTokenProperty.ALLOWED_SESSION_COUNT;
        if (sessionCreationHelper.doWeNeedToRemoveOldSession(numberOfSessionsInDB)) {
            sessions.sort(Comparator.comparing(UserSession::getCreatedDate));

            int sessionListSizeShouldBeForCreatingNewOne = allowedSessionCount - 1;

            List<UserSession> deletableSessions = new ArrayList<>();
            while (sessions.size() != sessionListSizeShouldBeForCreatingNewOne) {
                deletableSessions.add(sessions.remove(0));
            }
            // TODO : Even after deleting the session the User will be allowed to access Resource
            //  for token expiration duration. This is something we have to bear as per the business needs,
            //  Because for each request we can't validate the Token from the DB
            userSessionDetailRepository.deleteAllInBatch(deletableSessions);
            return deletableSessions;
        }

        return List.of();
    }


    @Transactional
    public JwtAuthenticationResponse refresh(RefreshTokenRequest refreshTokenRequest) {
        String accessToken = refreshTokenRequest.getAccessToken();
        throwExceptionIfAccessTokenIsNotExpired(accessToken);

        String refreshToken = refreshTokenRequest.getRefreshToken();
        validateRefreshTokenAsULID(refreshToken);

        String userName = jwtService.getUserNameFromJWT(accessToken);
        AppUser user = userRepository.findByEmail(userName).orElseThrow(
                () -> new JwtSecurityException(
                        JwtSecurityException.JWTErrorCode.USER_NOT_FOUND,
                        "User Not Found"
                )
        );

        List<UserSession> userSessions = user.getUserSessions();

        Optional<UserSession> oldSession = findInOldSessions(
                userSessions, accessToken, refreshToken
        );

        if (oldSession.isEmpty()) {
            throw new JwtSecurityException(
                    JwtSecurityException.JWTErrorCode.SESSION_NOT_FOUND,
                    "User Session For Refresh Not Found With Given Tokens"
            );
        }

        UserSession sessionToUpdate = oldSession.get();
        Date refreshTokenExpiryDate = sessionToUpdate.getRefreshTokenExpiryDate();

        if(refreshTokenExpiryDate.before(new Date())) {
            throw new JwtSecurityException(
                    JwtSecurityException.JWTErrorCode.REFRESH_TOKEN_EXPIRED,
                    "Refresh Token Is Expired, Create New Login Request"
            );
        }

        String newAccessToken = jwtService.generateAccessToken(new UserDetailsImpl(user));
        Ulid newRefreshToken = UlidCreator.getUlid(refreshTokenExpiryDate.getTime());

        sessionToUpdate.setActiveAccessToken(newAccessToken);
        sessionToUpdate.setActiveRefreshToken(newRefreshToken.toString());
        sessionToUpdate.setLastModifiedDate(new Date());
        sessionToUpdate.increaseTokenRefreshCount();

        userSessionDetailRepository.save(sessionToUpdate);

        return JwtAuthenticationResponse.builder()
                .accessToken(newAccessToken)
                .refreshToken(newRefreshToken.toString())
                .build();
    }

    private Optional<UserSession> findInOldSessions(List<UserSession> oldSessions,
                                                    String accessToken, String refreshToken) {

        return oldSessions.stream()
                .filter(userSessionDetail -> {
                    String activeAccessToken = userSessionDetail.getActiveAccessToken();
                    String activeRefreshToken = userSessionDetail.getActiveRefreshToken();

                    return StringUtils.equals(accessToken, activeAccessToken) &&
                            StringUtils.equals(refreshToken, activeRefreshToken);
                }).findFirst();
    }


    private void throwExceptionIfAccessTokenIsNotExpired(String accessToken) {
        Date accessTokenExpiredAt = jwtService.getTokenExpiryFromExpiredJWT(accessToken);
        if (accessTokenExpiredAt.after(new Date())) {
            throw new JwtSecurityException(
                    REFRESH_TOKEN_ONLY_ALLOWED_WITH_EXPIRED_TOKEN,
                    "Refreshing The Token Is Only Allowed When Access Token Is Expired"
            );
        }
    }

    private static void validateRefreshTokenAsULID(String refreshToken) {
        if (!Ulid.isValid(refreshToken)) {
            throw new JwtSecurityException(
                    JwtSecurityException.JWTErrorCode.INVALID_REFRESH_TOKEN,
                    "Invalid Refresh Token Provided"
            );
        }

        Date refreshTokenValidity = new Date(Ulid.from(refreshToken).getTime());
        if (refreshTokenValidity.before(new Date())) {
            throw new JwtSecurityException(
                    JwtSecurityException.JWTErrorCode.REFRESH_TOKEN_EXPIRED,
                    "Refresh Token Is Expired, Create New Login Request"
            );
        }
    }
}
