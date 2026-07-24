package com.monitoring.controller;

import com.monitoring.dto.IncidentDetailsDto;
import com.monitoring.service.IncidentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/incidents")
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:8080"})
@Tag(name = "Incidents", description = "Incident management endpoints")
public class IncidentController {
    private final IncidentService incidentService;

    public IncidentController(IncidentService incidentService) {
        this.incidentService = incidentService;
    }

    @PostMapping
    public ResponseEntity<IncidentDetailsDto> createIncident(@RequestBody IncidentDetailsDto dto) {
        IncidentDetailsDto created = incidentService.createIncident(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping
    public ResponseEntity<Page<IncidentDetailsDto>> getIncidents(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(incidentService.getIncidents(page, size));
    }

    @GetMapping("/application/{applicationName}")
    public ResponseEntity<Page<IncidentDetailsDto>> getIncidentsByApplication(
            @PathVariable String applicationName,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(incidentService.getIncidentsByApplication(applicationName, page, size));
    }

    @GetMapping("/severity/{severity}")
    public ResponseEntity<Page<IncidentDetailsDto>> getIncidentsBySeverity(
            @PathVariable String severity,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(incidentService.getIncidentsBySeverity(severity, page, size));
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<Page<IncidentDetailsDto>> getIncidentsByStatus(
            @PathVariable String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(incidentService.getIncidentsByStatus(status, page, size));
    }

    @GetMapping("/{incidentNo}")
    @Operation(summary = "Get incident by ID", description = "Retrieve a specific incident by its incident number")
    @ApiResponse(responseCode = "200", description = "Incident found", content = @Content(mediaType = "application/json", schema = @Schema(implementation = IncidentDetailsDto.class)))
    @ApiResponse(responseCode = "404", description = "Incident not found")
    public ResponseEntity<IncidentDetailsDto> getIncident(
            @Parameter(description = "Incident number (e.g., INC-2001)", example = "INC-2001", required = true)
            @PathVariable String incidentNo) {
        IncidentDetailsDto incident = incidentService.getIncidentByNumber(incidentNo);
        return incident != null ? ResponseEntity.ok(incident) : ResponseEntity.notFound().build();
    }

    @GetMapping("/{incidentNo}/rca-status")
    @Operation(summary = "Check RCA Status", description = "Check if AI analysis/RCA is available for incident (async processing)")
    public ResponseEntity<?> getRcaStatus(
            @Parameter(description = "Incident number", example = "INC-2001", required = true)
            @PathVariable String incidentNo) {
        IncidentDetailsDto incident = incidentService.getIncidentByNumber(incidentNo);
        if (incident == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(new java.util.HashMap<String, Object>() {{
            put("incidentNo", incidentNo);
            put("rcaAvailable", incident.getIncidentRca() != null);
            put("rcaId", incident.getRcaId());
            put("incidentRca", incident.getIncidentRca());
            put("message", incident.getIncidentRca() != null ?
                "✅ RCA analysis completed" :
                "⏳ RCA analysis in progress or not yet started");
        }});
    }

    @PutMapping("/{incidentNo}")
    public ResponseEntity<IncidentDetailsDto> updateIncident(
            @PathVariable String incidentNo,
            @RequestBody IncidentDetailsDto dto) {
        IncidentDetailsDto updated = incidentService.updateIncident(incidentNo, dto);
        return ResponseEntity.ok(updated);
    }
}
