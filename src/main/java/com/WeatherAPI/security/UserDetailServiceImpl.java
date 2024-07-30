package com.WeatherAPI.security;

import com.WeatherAPI.dao.UserRepository;
import com.WeatherAPI.entity.AppUser;
import com.WeatherAPI.security.dto.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserDetailServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        AppUser appUser = userRepository.findByEmail(username).orElseThrow(
                () -> new UsernameNotFoundException("User Not Found With UserName :: " + username)
        );
        return new UserDetailsImpl(appUser);
    }
}
