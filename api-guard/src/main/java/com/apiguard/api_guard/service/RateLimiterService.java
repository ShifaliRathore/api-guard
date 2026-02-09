package com.apiguard.api_guard.service;

public interface RateLimiterService {
    boolean isAllowed(String ipAddress);
}
