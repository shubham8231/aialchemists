package com.monitoring.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@Schema(description = "AI generated executive security assessment")
public class VertexSecurityAssessmentResponse {

    @Schema(description = "Concise executive summary")
    private String executiveSummary;

    @Schema(description = "Business impact statement")
    private String businessImpact;

    @Schema(description = "Estimated remediation timeline")
    private String estimatedRemediationTime;

    @Schema(description = "Confidence score from 0 to 100")
    private Integer confidenceScore;

    @Schema(description = "Overall recommendation for leadership")
    private String overallRecommendation;

    @Schema(description = "Prioritized remediation actions")
    @Builder.Default
    private List<PrioritizedRemediationItem> prioritizedRemediation = new ArrayList<>();
}
