package com.monitoring.dto;

import java.time.LocalDateTime;

public class IncidentRcaDto {
    private String rcaId;
    private String incidentNo;
    private String rootCause;
    private String recommendation;
    private String incidentRefs;
    private Double confidenceScore;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public IncidentRcaDto() {}

    public IncidentRcaDto(String rcaId, String incidentNo, String rootCause, String recommendation,
                         String incidentRefs, Double confidenceScore,
                         LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.rcaId = rcaId;
        this.incidentNo = incidentNo;
        this.rootCause = rootCause;
        this.recommendation = recommendation;
        this.incidentRefs = incidentRefs;
        this.confidenceScore = confidenceScore;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public String getRcaId() { return rcaId; }
    public void setRcaId(String rcaId) { this.rcaId = rcaId; }

    public String getIncidentNo() { return incidentNo; }
    public void setIncidentNo(String incidentNo) { this.incidentNo = incidentNo; }

    public String getRootCause() { return rootCause; }
    public void setRootCause(String rootCause) { this.rootCause = rootCause; }

    public String getRecommendation() { return recommendation; }
    public void setRecommendation(String recommendation) { this.recommendation = recommendation; }

    public String getIncidentRefs() { return incidentRefs; }
    public void setIncidentRefs(String incidentRefs) { this.incidentRefs = incidentRefs; }

    public Double getConfidenceScore() { return confidenceScore; }
    public void setConfidenceScore(Double confidenceScore) { this.confidenceScore = confidenceScore; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
