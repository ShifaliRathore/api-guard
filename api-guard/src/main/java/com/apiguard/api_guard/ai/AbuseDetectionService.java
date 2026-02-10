package com.apiguard.api_guard.ai;
public interface AbuseDetectionService {
    AbuseScoreResult analyze(String ipAddress, String endpoint);
}
