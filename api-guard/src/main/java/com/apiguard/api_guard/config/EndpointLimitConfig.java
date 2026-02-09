package com.apiguard.api_guard.config;

import java.util.Map;
public class EndpointLimitConfig {

    public static final Map<String, Integer> LIMITS = Map.of(
            "/hello", 5,
            "/login", 3
    );

    public static int getLimit(String endpoint) {
        return LIMITS.getOrDefault(endpoint, 10);
    }
}
