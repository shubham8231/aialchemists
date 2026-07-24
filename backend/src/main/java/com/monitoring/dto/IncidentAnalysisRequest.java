package com.monitoring.dto;

import java.util.List;

public class IncidentAnalysisRequest {
    private String action;
    private String errorType;
    private String incidentNo;
    private String details;

    public IncidentAnalysisRequest() {}

    public IncidentAnalysisRequest(String action, String errorType) {
        this.action = action;
        this.errorType = errorType;
    }

    public String getAction() { return action; }
    public void setAction(String action) { this.action = action; }

    public String getErrorType() { return errorType; }
    public void setErrorType(String errorType) { this.errorType = errorType; }

    public String getIncidentNo() { return incidentNo; }
    public void setIncidentNo(String incidentNo) { this.incidentNo = incidentNo; }

    public String getDetails() { return details; }
    public void setDetails(String details) { this.details = details; }
}
