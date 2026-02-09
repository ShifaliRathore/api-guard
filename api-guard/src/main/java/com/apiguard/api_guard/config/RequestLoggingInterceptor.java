package com.apiguard.api_guard.config;


import com.apiguard.api_guard.entity.ApiRequestLog;
import com.apiguard.api_guard.repository.ApiRequestLogRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.time.LocalDateTime;

@Component
public class RequestLoggingInterceptor implements HandlerInterceptor {

    private final ApiRequestLogRepository repository;

    public RequestLoggingInterceptor(ApiRequestLogRepository repository) {
        this.repository = repository;
    }

    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response,
                             Object handler) {

        ApiRequestLog log = new ApiRequestLog();
        log.setIpAddress(request.getRemoteAddr());
        log.setEndpoint(request.getRequestURI());
        log.setHttpMethod(request.getMethod());
        log.setRequestTime(LocalDateTime.now());

        repository.save(log);

        return true; // allow request to continue
    }
}
