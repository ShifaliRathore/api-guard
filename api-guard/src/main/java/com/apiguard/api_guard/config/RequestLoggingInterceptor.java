package com.apiguard.api_guard.config;

import com.apiguard.api_guard.ai.AbuseDetectionService;
import com.apiguard.api_guard.ai.AbuseLevel;
import com.apiguard.api_guard.ai.AbuseScoreResult;
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
import java.util.Optional;

@Component
public class RequestLoggingInterceptor implements HandlerInterceptor {

    private final ApiRequestLogRepository logRepository;
    private final BlockedIpRepository blockedIpRepository;
    private final RateLimiterService rateLimiterService;
    private final AbuseDetectionService abuseDetectionService;

    public RequestLoggingInterceptor(
            ApiRequestLogRepository logRepository,
            BlockedIpRepository blockedIpRepository,
            RateLimiterService rateLimiterService,
            AbuseDetectionService abuseDetectionService
    ) {
        this.logRepository = logRepository;
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

        // ✅ SKIP RATE LIMITING FOR ADMIN APIs
        if (endpoint.startsWith("/admin")) {
            return true;
        }

        // 1️⃣ Check if IP is already blocked
        Optional<BlockedIp> blockedIp =
                blockedIpRepository.findById(ipAddress);

        if (blockedIp.isPresent()
                && blockedIp.get().getBlockedUntil().isAfter(LocalDateTime.now())) {
            throw new RateLimitExceededException(
                    "Your IP is temporarily blocked due to abuse"
            );
        }

        // 2️⃣ Apply rate limiting
        boolean allowed =
                rateLimiterService.isAllowed(ipAddress, endpoint);

        if (!allowed) {
            blockedIpRepository.save(
                    new BlockedIp(
                            ipAddress,
                            LocalDateTime.now().plusMinutes(5)
                    )
            );

            throw new RateLimitExceededException(
                    "Too many requests. You have been temporarily blocked."
            );
        }
        // 1️⃣ AI Abuse Analysis FIRST
        AbuseScoreResult result =
                abuseDetectionService.analyze(ipAddress, endpoint);

// Optional: log behavior (VERY IMPORTANT for understanding)
        System.out.println(
                "IP: " + ipAddress +
                        " | Score: " + result.getScore() +
                        " | Level: " + result.getLevel()
        );

// 2️⃣ If MALICIOUS → BLOCK
        if (result.getLevel() == AbuseLevel.MALICIOUS) {

            blockedIpRepository.save(
                    new BlockedIp(
                            ipAddress,
                            LocalDateTime.now().plusMinutes(10)
                    )
            );

            throw new RateLimitExceededException(
                    "Malicious behavior detected. IP blocked."
            );
        }

// 3️⃣ Rate limiting ONLY for NORMAL / SUSPICIOUS
        if (!rateLimiterService.isAllowed(ipAddress, endpoint)) {
            // allow but mark as suspicious (no block yet)
            System.out.println("Rate limit crossed but not malicious yet");
        }



        // 3️⃣ Log request
        ApiRequestLog log = new ApiRequestLog();
        log.setIpAddress(ipAddress);
        log.setEndpoint(endpoint);
        log.setHttpMethod(request.getMethod());
        log.setRequestTime(LocalDateTime.now());

        logRepository.save(log);

        return true;
    }

}
