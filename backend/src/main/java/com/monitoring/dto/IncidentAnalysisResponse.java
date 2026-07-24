package com.monitoring.dto;

public class IncidentAnalysisResponse {
    private String message;
    private Object data;
    private Boolean success;
    private String nextAction;

    public IncidentAnalysisResponse() {
        this.success = true;
    }

    public IncidentAnalysisResponse(String message, Object data) {
        this.message = message;
        this.data = data;
        this.success = true;
    }

    public IncidentAnalysisResponse(String message, Object data, String nextAction) {
        this.message = message;
        this.data = data;
        this.nextAction = nextAction;
        this.success = true;
    }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public Object getData() { return data; }
    public void setData(Object data) { this.data = data; }

    public Boolean getSuccess() { return success; }
    public void setSuccess(Boolean success) { this.success = success; }

    public String getNextAction() { return nextAction; }
    public void setNextAction(String nextAction) { this.nextAction = nextAction; }
}
