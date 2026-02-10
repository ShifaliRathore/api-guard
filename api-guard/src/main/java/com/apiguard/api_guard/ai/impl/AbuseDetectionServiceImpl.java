package com.apiguard.api_guard.ai.impl;
import com.apiguard.api_guard.ai.*;
import com.apiguard.api_guard.repository.ApiRequestLogRepository;
import org.springframework.stereotype.Service;

@Service
public class AbuseDetectionServiceImpl implements AbuseDetectionService {

    private final ApiRequestLogRepository logRepository;

    public AbuseDetectionServiceImpl(ApiRequestLogRepository logRepository) {
        this.logRepository = logRepository;
    }

    @Override
    public AbuseScoreResult analyze(String ipAddress, String endpoint) {

        long totalRequests =
                logRepository.countByIpAddress(ipAddress);

        long violations = totalRequests / 3;   // was /5 (slower)

        long frequency = totalRequests;

        int endpointWeight = endpoint.contains("login") ? 5 : 2;

        int score =
                (int) (violations * 3 + frequency * 2 + endpointWeight);

        AbuseLevel level;
        if (score >= 12) {
            level = AbuseLevel.MALICIOUS;
        } else if (score >= 6) {
            level = AbuseLevel.SUSPICIOUS;
        } else {
            level = AbuseLevel.NORMAL;
        }

        return new AbuseScoreResult(score, level);
    }
}

