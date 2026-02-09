package com.apiguard.api_guard.repository;
import com.apiguard.api_guard.entity.ApiRequestLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;

public interface ApiRequestLogRepository extends JpaRepository<ApiRequestLog, Long> {
    long countByIpAddressAndRequestTimeAfter(
            String ipAddress,
            LocalDateTime time
    );
}
