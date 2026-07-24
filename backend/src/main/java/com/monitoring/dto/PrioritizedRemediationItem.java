package com.monitoring.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Prioritized remediation recommendation")
public class PrioritizedRemediationItem {

    @Schema(description = "Affected library name")
    private String library;

    @Schema(description = "Current vulnerable version")
    private String currentVersion;

    @Schema(description = "Suggested fixed version")
    private String fixedVersion;

    @Schema(description = "Priority order where 1 is highest")
    private Integer priority;

    @Schema(description = "Reason for this priority")
    private String reason;

    @Schema(description = "Estimated risk reduction percentage")
    private Integer riskReductionPercentage;
}
