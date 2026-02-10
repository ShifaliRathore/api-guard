package com.apiguard.api_guard.config;

import com.apiguard.api_guard.ai.AbuseLevel;
import com.apiguard.api_guard.ai.AbuseScoreResult;
import com.apiguard.api_guard.ai.AbuseDetectionService;
import com.apiguard.api_guard.entity.ApiRequestLog;
import com.apiguard.api_guard.entity.BlockedIp;
import com.apiguard.api_guard.exception.RateLimitExceededException;
import com.apiguard.api_guard.repository.ApiRequestLogRepository;
import com.apiguard.api_guard.repository.BlockedIpRepository;
import com.apiguard.api_guard.service.RateLimiterService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.time.LocalDateTime;

@Component
public class RequestLoggingInterceptor implements HandlerInterceptor {

    private final ApiRequestLogRepository apiRequestLogRepository;
    private final BlockedIpRepository blockedIpRepository;
    private final RateLimiterService rateLimiterService;
    private final AbuseDetectionService abuseDetectionService;

    public RequestLoggingInterceptor(
            ApiRequestLogRepository apiRequestLogRepository,
            BlockedIpRepository blockedIpRepository,
            RateLimiterService rateLimiterService,
            AbuseDetectionService abuseDetectionService
    ) {
        this.apiRequestLogRepository = apiRequestLogRepository;
        this.blockedIpRepository = blockedIpRepository;
        this.rateLimiterService = rateLimiterService;
        this.abuseDetectionService = abuseDetectionService;
    }

    @Override
    public boolean preHandle(
            HttpServletRequest request,
            HttpServletResponse response,
            Object handler
    ) {

        String ipAddress = request.getRemoteAddr();
        String endpoint = request.getRequestURI();
        String method = request.getMethod();

        // 1️⃣ Check if IP is already blocked
        blockedIpRepository.findById(ipAddress).ifPresent(blockedIp -> {
            if (blockedIp.getBlockedUntil().isAfter(LocalDateTime.now())) {
                throw new RateLimitExceededException(
                        "IP is temporarily blocked due to malicious activity"
                );
            }
        });

        // 2️⃣ AI Abuse Scoring
        AbuseScoreResult result =
                abuseDetectionService.analyze(ipAddress, endpoint);

        // Console log for understanding
        System.out.println(
                "IP: " + ipAddress +
                        " | Score: " + result.getScore() +
                        " | Level: " + result.getLevel()
        );

        // 3️⃣ Save request log WITH abuse level
        ApiRequestLog log = new ApiRequestLog();
        log.setEndpoint(endpoint);
        log.setHttpMethod(method);
        log.setIpAddress(ipAddress);
        log.setRequestTime(LocalDateTime.now());
        log.setAbuseLevel(result.getLevel().name());

        // ALWAYS save request first
        apiRequestLogRepository.save(log);

// THEN decide blocking
        if (result.getLevel() == AbuseLevel.MALICIOUS) {

            blockedIpRepository.save(
                    new BlockedIp(
                            ipAddress,
                            LocalDateTime.now().plusMinutes(10)
                    )
            );

            throw new RateLimitExceededException(
                    "Malicious activity detected. IP blocked."
            );
        }


        // 5️⃣ Soft rate limiting (do not block yet)
        rateLimiterService.isAllowed(ipAddress, endpoint);

        return true; // allow request
    }
}
