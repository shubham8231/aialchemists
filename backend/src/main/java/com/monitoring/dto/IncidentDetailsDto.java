package com.monitoring.dto;

public class IncidentDetailsDto {
    private String incidentNo;
    private String applicationName;
    private String environment;
    private String description;
    private String severity;
    private String status;
    private String errorType;
    private String rawLog;
    private String aiSummary;
    private String rcaId;
    private IncidentRcaDto incidentRca;
    private Boolean acknowledgementStatus;
    private String createdBy;

    public IncidentDetailsDto() {}

    public IncidentDetailsDto(String incidentNo, String applicationName, String environment,
                            String errorType, String severity, String status) {
        this.incidentNo = incidentNo;
        this.applicationName = applicationName;
        this.environment = environment;
        this.errorType = errorType;
        this.severity = severity;
        this.status = status;
    }

    public String getIncidentNo() { return incidentNo; }
    public void setIncidentNo(String incidentNo) { this.incidentNo = incidentNo; }

    public String getApplicationName() { return applicationName; }
    public void setApplicationName(String applicationName) { this.applicationName = applicationName; }

    public String getEnvironment() { return environment; }
    public void setEnvironment(String environment) { this.environment = environment; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getSeverity() { return severity; }
    public void setSeverity(String severity) { this.severity = severity; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getErrorType() { return errorType; }
    public void setErrorType(String errorType) { this.errorType = errorType; }

    public String getRawLog() { return rawLog; }
    public void setRawLog(String rawLog) { this.rawLog = rawLog; }

    public String getAiSummary() { return aiSummary; }
    public void setAiSummary(String aiSummary) { this.aiSummary = aiSummary; }

    public String getRcaId() { return rcaId; }
    public void setRcaId(String rcaId) { this.rcaId = rcaId; }

    public IncidentRcaDto getIncidentRca() { return incidentRca; }
    public void setIncidentRca(IncidentRcaDto incidentRca) { this.incidentRca = incidentRca; }

    public Boolean getAcknowledgementStatus() { return acknowledgementStatus; }
    public void setAcknowledgementStatus(Boolean acknowledgementStatus) { this.acknowledgementStatus = acknowledgementStatus; }

    public String getCreatedBy() { return createdBy; }
    public void setCreatedBy(String createdBy) { this.createdBy = createdBy; }
}
