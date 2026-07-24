package com.monitoring.controller;

import com.monitoring.dto.AuditLogDto;
import com.monitoring.service.AuditLogService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/audit-logs")
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:8080"})
@Tag(name = "Audit Logs", description = "Incident process audit logs")
public class AuditLogController {
    private final AuditLogService auditLogService;

    public AuditLogController(AuditLogService auditLogService) {
        this.auditLogService = auditLogService;
    }

    @GetMapping("/incident/{incidentNo}")
    @Operation(summary = "Get audit logs for incident", description = "Retrieve all audit logs for a specific incident")
    public ResponseEntity<List<AuditLogDto>> getAuditLogs(@PathVariable String incidentNo) {
        List<AuditLogDto> logs = auditLogService.getAuditLogs(incidentNo);
        return ResponseEntity.ok(logs);
    }

    @GetMapping("/incident/{incidentNo}/rca")
    @Operation(summary = "Get RCA audit logs", description = "Retrieve RCA analysis audit logs for incident")
    public ResponseEntity<List<AuditLogDto>> getRcaAuditLogs(@PathVariable String incidentNo) {
        List<AuditLogDto> logs = auditLogService.getRcaAuditLogs(incidentNo);
        return ResponseEntity.ok(logs);
    }

    @GetMapping("/incident/{incidentNo}/rca/status")
    @Operation(summary = "Get latest RCA status", description = "Get the current RCA analysis status")
    public ResponseEntity<AuditLogDto> getLatestRcaStatus(@PathVariable String incidentNo) {
        AuditLogDto status = auditLogService.getLatestRcaStatus(incidentNo);
        if (status == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(status);
    }
}
