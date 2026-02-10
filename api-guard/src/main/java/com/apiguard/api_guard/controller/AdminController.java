package com.apiguard.api_guard.controller;


import com.apiguard.api_guard.entity.BlockedIp;
import com.apiguard.api_guard.repository.ApiRequestLogRepository;
import com.apiguard.api_guard.repository.BlockedIpRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/admin")
public class AdminController {

    private final BlockedIpRepository blockedIpRepository;
    private final ApiRequestLogRepository logRepository;

    public AdminController(
            BlockedIpRepository blockedIpRepository,
            ApiRequestLogRepository logRepository
    ) {
        this.blockedIpRepository = blockedIpRepository;
        this.logRepository = logRepository;
    }

    // 1️⃣ View blocked IPs
    @GetMapping("/blocked-ips")
    public List<BlockedIp> getBlockedIps() {
        return blockedIpRepository.findAll();
    }

    // 2️⃣ Request count per IP
    @GetMapping("/requests-per-ip")
    public Map<String, Long> getRequestCountPerIp() {
        return logRepository.findAll()
                .stream()
                .collect(Collectors.groupingBy(
                        log -> log.getIpAddress(),
                        Collectors.counting()
                ));
    }

    // 3️⃣ Request count per endpoint
    @GetMapping("/requests-per-endpoint")
    public Map<String, Long> getRequestCountPerEndpoint() {
        return logRepository.findAll()
                .stream()
                .collect(Collectors.groupingBy(
                        log -> log.getEndpoint(),
                        Collectors.counting()
                ));
    }
}
