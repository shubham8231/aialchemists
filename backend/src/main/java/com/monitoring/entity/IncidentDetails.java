package com.monitoring.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
    @Table(name = "incident_details")
public class IncidentDetails {
    @Id
    @Column(name = "incident_no", length = 100)
    private String incidentNo;

    @Column(nullable = false)
    private String applicationName;

    @Column(nullable = false)
    private String environment;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false)
    private String severity;

    @Column(nullable = false)
    private String status;

    @Column(nullable = false)
    private String errorType;

    @Column(columnDefinition = "TEXT")
    private String rawLog;

    @Column(columnDefinition = "TEXT")
    private String aiSummary;

    @Column(name = "rca_id")
    private String rcaId;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "rca_id", referencedColumnName = "rca_id", insertable = false, updatable = false)
    private IncidentRca incidentRca;

    @Column(nullable = false)
    private Boolean acknowledgementStatus = false;

    @Column(nullable = false)
    private String createdBy;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    public IncidentDetails() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.acknowledgementStatus = false;
    }

    // Getters and Setters
    public String getIncidentNo() {
        return incidentNo;
    }

    public void setIncidentNo(String incidentNo) {
        this.incidentNo = incidentNo;
    }

    public String getApplicationName() {
        return applicationName;
    }

    public void setApplicationName(String applicationName) {
        this.applicationName = applicationName;
    }

    public String getEnvironment() {
        return environment;
    }

    public void setEnvironment(String environment) {
        this.environment = environment;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getSeverity() {
        return severity;
    }

    public void setSeverity(String severity) {
        this.severity = severity;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getErrorType() {
        return errorType;
    }

    public void setErrorType(String errorType) {
        this.errorType = errorType;
    }

    public String getRawLog() {
        return rawLog;
    }

    public void setRawLog(String rawLog) {
        this.rawLog = rawLog;
    }

    public String getAiSummary() {
        return aiSummary;
    }

    public void setAiSummary(String aiSummary) {
        this.aiSummary = aiSummary;
    }

    public String getRcaId() {
        return rcaId;
    }

    public void setRcaId(String rcaId) {
        this.rcaId = rcaId;
    }

    public IncidentRca getIncidentRca() {
        return incidentRca;
    }

    public void setIncidentRca(IncidentRca incidentRca) {
        this.incidentRca = incidentRca;
    }

    public Boolean getAcknowledgementStatus() {
        return acknowledgementStatus;
    }

    public void setAcknowledgementStatus(Boolean acknowledgementStatus) {
        this.acknowledgementStatus = acknowledgementStatus;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
