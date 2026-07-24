package com.monitoring.dto;

public class AuditLogDto {
    private Long id;
    private String incidentNo;
    private String processType;
    private String status;
    private String message;
    private String details;
    private Long durationMs;
    private String createdAt;

    public AuditLogDto() {}

    public AuditLogDto(String incidentNo, String processType, String status, String message) {
        this.incidentNo = incidentNo;
        this.processType = processType;
        this.status = status;
        this.message = message;
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

    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }
}
