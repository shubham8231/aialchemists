package com.monitoring.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request payload for triggering a vulnerability scan")
public class ScanRequest {

    @NotBlank(message = "Application name must not be blank")
    @Schema(description = "Name of the application to scan", example = "IAM-Service")
    private String applicationName;
}
