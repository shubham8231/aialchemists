package com.monitoring.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "incident_audit_log", indexes = {
    @Index(name = "idx_incident_no", columnList = "incident_no"),
    @Index(name = "idx_created_at", columnList = "created_at")
})
public class IncidentAuditLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "incident_no", nullable = false)
    private String incidentNo;

    @Column(name = "process_type", nullable = false)
    private String processType; // RCA_ANALYSIS, EMAIL_SYNC, LOG_MONITORING

    @Column(name = "status", nullable = false)
    private String status; // PENDING, IN_PROGRESS, COMPLETED, FAILED

    @Column(columnDefinition = "TEXT")
    private String message;

    @Column(columnDefinition = "TEXT")
    private String details;

    @Column(name = "duration_ms")
    private Long durationMs;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    public IncidentAuditLog() {
        this.createdAt = LocalDateTime.now();
    }

    public IncidentAuditLog(String incidentNo, String processType, String status, String message) {
        this.incidentNo = incidentNo;
        this.processType = processType;
        this.status = status;
        this.message = message;
        this.createdAt = LocalDateTime.now();
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getIncidentNo() { return incidentNo; }
    public void setIncidentNo(String incidentNo) { this.incidentNo = incidentNo; }

    public String getProcessType() { return processType; }
    public void setProcessType(String processType) { this.processType = processType; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public String getDetails() { return details; }
    public void setDetails(String details) { this.details = details; }

    public Long getDurationMs() { return durationMs; }
    public void setDurationMs(Long durationMs) { this.durationMs = durationMs; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
