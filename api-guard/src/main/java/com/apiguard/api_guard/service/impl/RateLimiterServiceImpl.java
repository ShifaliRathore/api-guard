package com.apiguard.api_guard.service.impl;

import com.apiguard.api_guard.repository.ApiRequestLogRepository;
import com.apiguard.api_guard.service.RateLimiterService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class RateLimiterServiceImpl implements RateLimiterService {

    private static final int MAX_REQUESTS = 5;
    private static final int TIME_WINDOW_MINUTES = 1;

    private final ApiRequestLogRepository repository;

    public RateLimiterServiceImpl(ApiRequestLogRepository repository) {
        this.repository = repository;
    }

    @Override
    public boolean isAllowed(String ipAddress) {
        LocalDateTime windowStart = LocalDateTime.now()
                .minusMinutes(TIME_WINDOW_MINUTES);

        long requestCount =
                repository.countByIpAddressAndRequestTimeAfter(
                        ipAddress,
                        windowStart
                );

        return requestCount < MAX_REQUESTS;
    }
}
