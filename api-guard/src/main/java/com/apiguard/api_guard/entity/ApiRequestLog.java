package com.apiguard.api_guard.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "api_request_logs")
public class ApiRequestLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String endpoint;

    @Column(name = "http_method")
    private String httpMethod;

    @Column(name = "ip_address")
    private String ipAddress;

    @Column(name = "request_time")
    private LocalDateTime requestTime;

    // ✅ NEW FIELD
    @Column(name = "abuse_level")
    private String abuseLevel;

    // getters & setters

    public Long getId() {
        return id;
    }

    public String getEndpoint() {
        return endpoint;
    }

    public void setEndpoint(String endpoint) {
        this.endpoint = endpoint;
    }

    public String getHttpMethod() {
        return httpMethod;
    }

    public void setHttpMethod(String httpMethod) {
        this.httpMethod = httpMethod;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public LocalDateTime getRequestTime() {
        return requestTime;
    }

    public void setRequestTime(LocalDateTime requestTime) {
        this.requestTime = requestTime;
    }

    public String getAbuseLevel() {
        return abuseLevel;
    }

    public void setAbuseLevel(String abuseLevel) {
        this.abuseLevel = abuseLevel;
    }
}
