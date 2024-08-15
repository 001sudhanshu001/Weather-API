package com.WeatherAPI.security;

import com.WeatherAPI.security.jwt.JwtAuthenticationEntryPoint;
import com.WeatherAPI.security.jwt.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import static org.springframework.security.config.http.SessionCreationPolicy.STATELESS;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfiguration {

    private final UserDetailsService userDetailsService;
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable)
                .exceptionHandling(configurer -> configurer.authenticationEntryPoint(jwtAuthenticationEntryPoint))
                .authorizeHttpRequests(
                        request -> request.requestMatchers("/api/v1/auth/signin",
                                        "/api/v1/auth/signup", "/api/v1/auth/refresh",
                                        "/api/v1/auth/signin-exclusively").permitAll()
                                .requestMatchers("/api/v1/auth/create-administrative-user").hasAuthority("SUPER_ADMIN")

                                .requestMatchers(HttpMethod.POST, "/v1/locations/**").hasAnyAuthority("ADMIN", "SUPER_ADMIN")
                                .requestMatchers(HttpMethod.PUT, "/v1/locations/**").hasAnyAuthority("ADMIN", "SUPER_ADMIN")
                                .requestMatchers(HttpMethod.DELETE, "/v1/locations/**").hasAnyAuthority("ADMIN", "SUPER_ADMIN")
                                .requestMatchers(HttpMethod.GET, "/v1/locations/**").hasAnyAuthority("ADMIN", "SUPER_ADMIN")

                                .requestMatchers(HttpMethod.PUT, "/v1/daily/**").hasAnyAuthority("ADMIN", "SUPER_ADMIN")
                                .requestMatchers(HttpMethod.GET, "/v1/daily/**").permitAll()

                                .requestMatchers(HttpMethod.PUT, "/v1/full/**").hasAnyAuthority("ADMIN", "SUPER_ADMIN")
                                .requestMatchers(HttpMethod.GET, "/v1/full/**").permitAll()

                                .requestMatchers(HttpMethod.PUT, "/v1/realtime/**").hasAnyAuthority("ADMIN", "SUPER_ADMIN")
                                .requestMatchers(HttpMethod.GET, "/v1/realtime/**").permitAll()

                                .requestMatchers(HttpMethod.PUT, "/v1/hourly/**").hasAnyAuthority("ADMIN", "SUPER_ADMIN")
                                .requestMatchers(HttpMethod.GET, "/v1/hourly/**").permitAll()

                )
                .sessionManagement(manager -> manager.sessionCreationPolicy(STATELESS))
                .authenticationProvider(authenticationProvider())
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config)
            throws Exception {
        return config.getAuthenticationManager();
    }
}
