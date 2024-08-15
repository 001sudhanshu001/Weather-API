package com.WeatherAPI.controller;

import com.WeatherAPI.dao.UserRepository;
import com.WeatherAPI.entity.AppUser;
import com.WeatherAPI.security.dto.*;
import com.WeatherAPI.security.enums.Role;
import com.WeatherAPI.service.AuthenticationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.util.Set;

@Slf4j
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthenticationController {

    private static final Set<String> VALID_ROLES = Set.of(
            "ADMIN",
            "WEATHER_STATION"
    );

    private final UserRepository userRepository;
    private final AuthenticationService authenticationService;
    private final ModelMapper modelMapper;


    @PreAuthorize("hasAuthority('SUPER_ADMIN')")
    @PostMapping("/create-administrative-user")
    public ResponseEntity<?> createAdmin(
            @RequestBody @Valid SignUpRequestForAdminUser requestForAdminUser) {

        String email = requestForAdminUser.getEmail();
        Optional<AppUser> byEmail = userRepository.findByEmail(email);
        if(byEmail.isPresent()) {
            return new ResponseEntity<>("This Email already exist", HttpStatus.CONFLICT);
        }

        Role userRole = requestForAdminUser.getRole();
        if(!VALID_ROLES.contains(userRole.name())) {
            return ResponseEntity.badRequest().body("Role " + userRole + " is invalid");
        }

        SignUpRequest request = modelMapper.map(requestForAdminUser, SignUpRequest.class);
        return ResponseEntity.ok(authenticationService.signup(request, userRole));
    }

    @PostMapping("/signup")
    public ResponseEntity<?> signup(
            @RequestBody @Valid SignUpRequest request) {

        String email = request.getEmail();
        Optional<AppUser> byEmail = userRepository.findByEmail(email);
        if(byEmail.isPresent()) {
            return new ResponseEntity<>("This Email already exist", HttpStatus.CONFLICT);
        }
        return ResponseEntity.ok(authenticationService.signup(request, Role.WEB_USER));
    }

    @PostMapping("/signin")
    public ResponseEntity<JwtAuthenticationResponse> signin(
            @RequestBody @Valid SigninRequest request) {

        JwtAuthenticationResponse jwtAuthenticationResponse =
                authenticationService.signin(request);

        return ResponseEntity.ok(jwtAuthenticationResponse);
    }

    @PostMapping("/signin-exclusively")
    public ResponseEntity<JwtAuthenticationResponse> signInExclusively(
            @RequestBody @Valid SigninRequest request) {

        JwtAuthenticationResponse jwtAuthenticationResponse =
                authenticationService.signinExclusively(request);

        return ResponseEntity.ok(jwtAuthenticationResponse);
    }

    @PostMapping("/refresh")
    public ResponseEntity<JwtAuthenticationResponse> refresh(
            @RequestBody @Valid RefreshTokenRequest refreshTokenRequest) {

        return ResponseEntity.ok(authenticationService.refresh(refreshTokenRequest));
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logoutUser(@Valid @RequestBody LogOutRequest logOutRequest) {
        String userName = authenticationService.logout(logOutRequest);

        return ResponseEntity.ok("User has successfully logged out from the system!");
    }

}
