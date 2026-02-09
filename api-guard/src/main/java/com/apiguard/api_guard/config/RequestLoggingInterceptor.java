package com.apiguard.api_guard.config;


import com.apiguard.api_guard.entity.ApiRequestLog;
import com.apiguard.api_guard.exception.RateLimitExceededException;
import com.apiguard.api_guard.repository.ApiRequestLogRepository;
import com.apiguard.api_guard.service.RateLimiterService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.time.LocalDateTime;

@Component
public class RequestLoggingInterceptor implements HandlerInterceptor {

    private final ApiRequestLogRepository repository;
    private final RateLimiterService rateLimiterService;

    public RequestLoggingInterceptor(
            ApiRequestLogRepository repository,
            RateLimiterService rateLimiterService
    ) {
        this.repository = repository;
        this.rateLimiterService = rateLimiterService;
    }

    @Override
    public boolean preHandle(
            HttpServletRequest request,
            HttpServletResponse response,
            Object handler
    ) {

        String ipAddress = request.getRemoteAddr();

        if (!rateLimiterService.isAllowed(ipAddress)) {
            throw new RateLimitExceededException(
                    "Too many requests. Please try again later."
            );
        }

        ApiRequestLog log = new ApiRequestLog();
        log.setIpAddress(ipAddress);
        log.setEndpoint(request.getRequestURI());
        log.setHttpMethod(request.getMethod());
        log.setRequestTime(LocalDateTime.now());

        repository.save(log);

        return true;
    }
}
