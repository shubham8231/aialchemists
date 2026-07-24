package com.monitoring.dto;

public class AiAnalysisResultDto {
    private String rootCause;
    private String recommendation;
    private Double confidenceScore;
    private String analysis;
    private String relatedIncidents;

    public AiAnalysisResultDto() {}

    public AiAnalysisResultDto(String rootCause, String recommendation, Double confidenceScore, String analysis) {
        this.rootCause = rootCause;
        this.recommendation = recommendation;
        this.confidenceScore = confidenceScore;
        this.analysis = analysis;
    }

    public String getRootCause() {
        return rootCause;
    }

    public void setRootCause(String rootCause) {
        this.rootCause = rootCause;
    }

    public String getRecommendation() {
        return recommendation;
    }

    public void setRecommendation(String recommendation) {
        this.recommendation = recommendation;
    }

    public Double getConfidenceScore() {
        return confidenceScore;
    }

    public void setConfidenceScore(Double confidenceScore) {
        this.confidenceScore = confidenceScore;
    }

    public String getAnalysis() {
        return analysis;
    }

    public void setAnalysis(String analysis) {
        this.analysis = analysis;
    }

    public String getRelatedIncidents() {
        return relatedIncidents;
    }

    public void setRelatedIncidents(String relatedIncidents) {
        this.relatedIncidents = relatedIncidents;
    }
}
