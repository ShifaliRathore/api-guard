package com.apiguard.api_guard.entity;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import java.time.LocalDateTime;

@Entity
public class BlockedIp {

    @Id
    private String ipAddress;

    private LocalDateTime blockedUntil;

    public BlockedIp() {
    }

    public BlockedIp(String ipAddress, LocalDateTime blockedUntil) {
        this.ipAddress = ipAddress;
        this.blockedUntil = blockedUntil;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public LocalDateTime getBlockedUntil() {
        return blockedUntil;
    }

    public void setBlockedUntil(LocalDateTime blockedUntil) {
        this.blockedUntil = blockedUntil;
    }
}
