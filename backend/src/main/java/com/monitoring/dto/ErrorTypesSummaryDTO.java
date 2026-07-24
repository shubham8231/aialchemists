package com.monitoring.dto;

import java.util.List;

public class ErrorTypesSummaryDTO {
    private String errorType;
    private Long count;
    private String description;
    private List<String> recentIncidents;

    public ErrorTypesSummaryDTO() {}

    public ErrorTypesSummaryDTO(String errorType, Long count, String description) {
        this.errorType = errorType;
        this.count = count;
        this.description = description;
    }

    public String getErrorType() { return errorType; }
    public void setErrorType(String errorType) { this.errorType = errorType; }

    public Long getCount() { return count; }
    public void setCount(Long count) { this.count = count; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public List<String> getRecentIncidents() { return recentIncidents; }
    public void setRecentIncidents(List<String> recentIncidents) { this.recentIncidents = recentIncidents; }
}
