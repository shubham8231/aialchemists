package com.monitoring.service;

import com.monitoring.dto.AuditLogDto;
import com.monitoring.entity.IncidentAuditLog;
import com.monitoring.repository.IncidentAuditLogRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AuditLogService {
    private static final Logger logger = LoggerFactory.getLogger(AuditLogService.class);
    private final IncidentAuditLogRepository auditLogRepository;

    public AuditLogService(IncidentAuditLogRepository auditLogRepository) {
        this.auditLogRepository = auditLogRepository;
    }

    // Log RCA Analysis Started
    public void logRcaStarted(String incidentNo) {
        logAudit(incidentNo, "RCA_ANALYSIS", "IN_PROGRESS", "AI RCA analysis started");
        logger.info("RCA analysis started for incident: {}", incidentNo);
    }

    // Log RCA Analysis Completed
    public void logRcaCompleted(String incidentNo, String rcaId, long durationMs) {
        IncidentAuditLog log = new IncidentAuditLog(
            incidentNo,
            "RCA_ANALYSIS",
            "COMPLETED",
            "AI RCA analysis completed successfully"
        );
        log.setDurationMs(durationMs);
        log.setDetails("RCA ID: " + rcaId);
        auditLogRepository.save(log);
        logger.info("RCA analysis completed for incident: {} in {}ms", incidentNo, durationMs);
    }

    // Log RCA Analysis Failed
    public void logRcaFailed(String incidentNo, String error, long durationMs) {
        IncidentAuditLog log = new IncidentAuditLog(
            incidentNo,
            "RCA_ANALYSIS",
            "FAILED",
            "AI RCA analysis failed"
        );
        log.setDurationMs(durationMs);
        log.setDetails(error);
        auditLogRepository.save(log);
        logger.error("RCA analysis failed for incident: {}: {}", incidentNo, error);
    }

    // Log RCA Finding Similar Incidents
    public void logFindingSimilarIncidents(String incidentNo) {
        logAudit(incidentNo, "RCA_ANALYSIS", "IN_PROGRESS", "Searching for similar incidents in database");
    }

    // Log Calling Vertex AI
    public void logCallingVertexAi(String incidentNo) {
        logAudit(incidentNo, "RCA_ANALYSIS", "IN_PROGRESS", "Calling Vertex AI Gemini API for analysis");
    }

    // Log Parsing Response
    public void logParsingResponse(String incidentNo) {
        logAudit(incidentNo, "RCA_ANALYSIS", "IN_PROGRESS", "Parsing AI response and extracting insights");
    }

    // Log Saving RCA
    public void logSavingRca(String incidentNo) {
        logAudit(incidentNo, "RCA_ANALYSIS", "IN_PROGRESS", "Saving RCA to database and linking to incident");
    }

    // Generic audit log
    public void logAudit(String incidentNo, String processType, String status, String message) {
        IncidentAuditLog log = new IncidentAuditLog(incidentNo, processType, status, message);
        auditLogRepository.save(log);
    }

    // Get all audit logs for incident
    public List<AuditLogDto> getAuditLogs(String incidentNo) {
        return auditLogRepository.findByIncidentNoOrderByCreatedAtDesc(incidentNo)
            .stream()
            .map(this::convertToDto)
            .collect(Collectors.toList());
    }

    // Get RCA audit logs only
    public List<AuditLogDto> getRcaAuditLogs(String incidentNo) {
        return auditLogRepository.findByIncidentNoAndProcessTypeOrderByCreatedAtDesc(incidentNo, "RCA_ANALYSIS")
            .stream()
            .map(this::convertToDto)
            .collect(Collectors.toList());
    }

    // Get latest RCA status
    public AuditLogDto getLatestRcaStatus(String incidentNo) {
        List<IncidentAuditLog> logs = auditLogRepository.findByIncidentNoAndProcessTypeOrderByCreatedAtDesc(incidentNo, "RCA_ANALYSIS");
        if (logs.isEmpty()) {
            return null;
        }
        return convertToDto(logs.get(0));
    }

    private AuditLogDto convertToDto(IncidentAuditLog log) {
        AuditLogDto dto = new AuditLogDto();
        dto.setId(log.getId());
        dto.setIncidentNo(log.getIncidentNo());
        dto.setProcessType(log.getProcessType());
        dto.setStatus(log.getStatus());
        dto.setMessage(log.getMessage());
        dto.setDetails(log.getDetails());
        dto.setDurationMs(log.getDurationMs());
        dto.setCreatedAt(log.getCreatedAt().toString());
        return dto;
    }
}
