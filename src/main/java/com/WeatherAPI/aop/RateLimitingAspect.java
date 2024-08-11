package com.WeatherAPI.aop;

import com.WeatherAPI.exception.RateLimitExceededException;
import com.WeatherAPI.utils.CommonUtility;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.concurrent.TimeUnit;

import static com.WeatherAPI.constants.RateLimitingConstants.*;

@Aspect
@Component
@RequiredArgsConstructor
public class RateLimitingAspect {

    private final StringRedisTemplate redisTemplate;


    // TODO -> This Rate Limiting is 5 Request in 60 seconds on all api
    // As per Needs we can concat LIMIT_KEY_PREFIX concat with api path if we
    // want Rate Limiting per single Api by a single user instead of Limiting all APIs
    // The key will be ::
    // String key = LIMIT_KEY_PREFIX  + request.getServletPath() + clientIp;

    @Before("@annotation(rateLimited)")
    public void checkRateLimit(RateLimited rateLimited) {
        HttpServletRequest request = getCurrentHttpRequest();
        String clientIp = CommonUtility.getIpAddress(request);
        
        String userName = getAuthenticatedUserName();
        if(userName != null) {
            String key = LIMIT_KEY_PREFIX + userName;

            Long requestCount = redisTemplate.opsForValue().increment(key, 1);

            if (requestCount != null && requestCount == 1) {
                redisTemplate.expire(key, TIME_WINDOW, TimeUnit.SECONDS);
            }

            if (requestCount != null && requestCount > MAX_REQUESTS_FOR_AUTHENTICATED) {
                throw new RateLimitExceededException("Rate limit exceeded. Please try again later.");
            }
        } else {
            String key = LIMIT_KEY_PREFIX + clientIp;

            Long requestCount = redisTemplate.opsForValue().increment(key, 1);

            if (requestCount != null && requestCount == 1) {
                redisTemplate.expire(key, TIME_WINDOW, TimeUnit.SECONDS);
            }

            if (requestCount != null && requestCount > MAX_REQUESTS) {
                throw new RateLimitExceededException("Rate limit exceeded. Please try again later.");
            }
        }

    }

    private HttpServletRequest getCurrentHttpRequest() {
        return ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes())
                .getRequest();
    }

    private String getAuthenticatedUserName() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userName = null;
        if (authentication != null && authentication.isAuthenticated()) {
            userName = authentication.getName();
        }

        return userName;
    }

}