package com.monitoring.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Vulnerability scan result for a single dependency")
public class DependencyScanResult {

    @Schema(description = "Package name of the dependency", example = "org.springframework.boot:spring-boot-starter-web")
    private String packageName;

    @Schema(description = "Ecosystem of the dependency", example = "Maven")
    private String ecosystem;

    @Schema(description = "Version of the dependency", example = "3.4.5")
    private String version;

    @Schema(description = "Scope of the dependency", example = "compile")
    private String dependencyScope;

    @Schema(description = "Total number of vulnerabilities found")
    private int vulnerabilityCount;

    @Schema(description = "List of vulnerabilities found for this dependency")
    private List<OsvVulnerability> vulnerabilities;
}
