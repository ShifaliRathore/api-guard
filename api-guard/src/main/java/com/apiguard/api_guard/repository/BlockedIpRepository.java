package com.apiguard.api_guard.repository;


import com.apiguard.api_guard.entity.BlockedIp;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BlockedIpRepository extends JpaRepository<BlockedIp, String> {
}
