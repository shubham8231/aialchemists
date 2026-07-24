package com.monitoring.service;

import com.monitoring.dto.AiAnalysisResultDto;
import com.monitoring.dto.IncidentDetailsDto;
import com.monitoring.dto.IncidentRcaDto;
import com.monitoring.entity.IncidentDetails;
import com.monitoring.entity.IncidentRca;
import com.monitoring.repository.IncidentDetailsRepository;
import com.monitoring.repository.IncidentRcaRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;

@Service
public class IncidentService {
    private static final Logger logger = LoggerFactory.getLogger(IncidentService.class);
    private final IncidentDetailsRepository incidentDetailsRepository;
    private final IncidentRcaRepository incidentRcaRepository;
    private final VertexAiIncidentAnalysisService aiAnalysisService;

    public IncidentService(IncidentDetailsRepository incidentDetailsRepository,
                          IncidentRcaRepository incidentRcaRepository,
                          VertexAiIncidentAnalysisService aiAnalysisService) {
        this.incidentDetailsRepository = incidentDetailsRepository;
        this.incidentRcaRepository = incidentRcaRepository;
        this.aiAnalysisService = aiAnalysisService;
    }

    public IncidentDetailsDto createIncident(IncidentDetailsDto dto) {
        IncidentDetails incident = new IncidentDetails();
        incident.setIncidentNo(generateIncidentNumber());
        incident.setApplicationName(dto.getApplicationName());
        incident.setEnvironment(dto.getEnvironment());
        incident.setDescription(dto.getDescription());
        incident.setSeverity(dto.getSeverity());
        incident.setStatus(dto.getStatus() != null ? dto.getStatus() : "New");
        incident.setErrorType(dto.getErrorType());
        incident.setRawLog(dto.getRawLog());
        incident.setAiSummary(dto.getAiSummary());
        incident.setCreatedBy(dto.getCreatedBy() != null ? dto.getCreatedBy() : "SYSTEM");
        incident.setAcknowledgementStatus(false);
        incident.setCreatedAt(LocalDateTime.now());
        incident.setUpdatedAt(LocalDateTime.now());

        IncidentDetails saved = incidentDetailsRepository.save(incident);

        // Analyze incident with AI and auto-generate RCA
        analyzeAndLinkRca(saved);

        return convertToDto(saved);
    }

    @Async("aiAnalysisExecutor")
    public void analyzeAndLinkRca(IncidentDetails incident) {
        logger.info("Starting async AI analysis for incident: {}", incident.getIncidentNo());
        try {
            AiAnalysisResultDto aiResult = aiAnalysisService.analyzeIncident(incident);
            if (aiResult != null && aiResult.getRootCause() != null) {
                IncidentRca rca = new IncidentRca();
                rca.setRcaId(incident.getIncidentNo() + "-RCA");
                rca.setIncidentNo(incident.getIncidentNo());
                rca.setRootCause(truncateString(aiResult.getRootCause(), 2000));
                rca.setRecommendation(truncateString(aiResult.getRecommendation(), 2000));
                rca.setIncidentRefs(truncateString(aiResult.getRelatedIncidents(), 500));
                rca.setConfidenceScore(aiResult.getConfidenceScore());
                rca.setCreatedAt(LocalDateTime.now());
                rca.setUpdatedAt(LocalDateTime.now());

                IncidentRca savedRca = incidentRcaRepository.save(rca);
                incident.setRcaId(savedRca.getRcaId());
                incident.setUpdatedAt(LocalDateTime.now());
                incidentDetailsRepository.save(incident);
                logger.info("AI analysis completed for incident: {} with RCA: {}",
                    incident.getIncidentNo(), savedRca.getRcaId());
            } else {
                logger.warn("AI analysis returned no results for incident: {}", incident.getIncidentNo());
            }
        } catch (Exception e) {
            logger.error("Error during async AI analysis for incident: {}", incident.getIncidentNo(), e);
        }
    }

    public IncidentRca createRca(IncidentRcaDto rcaDto) {
        IncidentRca rca = new IncidentRca();
        rca.setRootCause(rcaDto.getRootCause());
        rca.setRecommendation(rcaDto.getRecommendation());
        rca.setIncidentRefs(rcaDto.getIncidentRefs());
        rca.setConfidenceScore(rcaDto.getConfidenceScore() != null ? rcaDto.getConfidenceScore() : 0.0);
        rca.setCreatedAt(LocalDateTime.now());
        rca.setUpdatedAt(LocalDateTime.now());
        return incidentRcaRepository.save(rca);
    }

    public Page<IncidentDetailsDto> getIncidents(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return incidentDetailsRepository.findAll(pageable).map(this::convertToDto);
    }

    public Page<IncidentDetailsDto> getIncidentsByApplication(String applicationName, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return incidentDetailsRepository.findByApplicationName(applicationName, pageable)
                .map(this::convertToDto);
    }

    public Page<IncidentDetailsDto> getIncidentsBySeverity(String severity, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return incidentDetailsRepository.findBySeverity(severity, pageable)
                .map(this::convertToDto);
    }

    public Page<IncidentDetailsDto> getIncidentsByStatus(String status, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return incidentDetailsRepository.findByStatus(status, pageable)
                .map(this::convertToDto);
    }

    public IncidentDetailsDto getIncidentByNumber(String incidentNo) {
        return incidentDetailsRepository.findByIncidentNo(incidentNo)
                .map(this::convertToDto)
                .orElse(null);
    }

    public IncidentDetailsDto updateIncident(String incidentNo, IncidentDetailsDto dto) {
        IncidentDetails incident = incidentDetailsRepository.findByIncidentNo(incidentNo)
                .orElseThrow(() -> new RuntimeException("Incident not found"));

        incident.setDescription(dto.getDescription());
        incident.setStatus(dto.getStatus());
        incident.setAiSummary(dto.getAiSummary());
        incident.setAcknowledgementStatus(dto.getAcknowledgementStatus());
        incident.setUpdatedAt(LocalDateTime.now());

        IncidentDetails updated = incidentDetailsRepository.save(incident);
        return convertToDto(updated);
    }

    private String generateIncidentNumber() {
        return "INC-" + System.currentTimeMillis() % 10000;
    }

    private String truncateString(String str, int maxLength) {
        if (str == null) return null;
        return str.length() > maxLength ? str.substring(0, maxLength) : str;
    }

    private IncidentDetailsDto convertToDto(IncidentDetails incident) {
        IncidentDetailsDto dto = new IncidentDetailsDto();
        dto.setIncidentNo(incident.getIncidentNo());
        dto.setApplicationName(incident.getApplicationName());
        dto.setEnvironment(incident.getEnvironment());
        dto.setDescription(incident.getDescription());
        dto.setSeverity(incident.getSeverity());
        dto.setStatus(incident.getStatus());
        dto.setErrorType(incident.getErrorType());
        dto.setRawLog(incident.getRawLog());
        dto.setAiSummary(incident.getAiSummary());
        dto.setRcaId(incident.getRcaId());
        dto.setCreatedBy(incident.getCreatedBy());
        dto.setAcknowledgementStatus(incident.getAcknowledgementStatus());

        // Include RCA data if available
        if (incident.getIncidentRca() != null) {
            IncidentRcaDto rcaDto = new IncidentRcaDto();
            rcaDto.setRcaId(incident.getIncidentRca().getRcaId());
            rcaDto.setIncidentNo(incident.getIncidentRca().getIncidentNo());
            rcaDto.setRootCause(incident.getIncidentRca().getRootCause());
            rcaDto.setRecommendation(incident.getIncidentRca().getRecommendation());
            rcaDto.setIncidentRefs(incident.getIncidentRca().getIncidentRefs());
            rcaDto.setConfidenceScore(incident.getIncidentRca().getConfidenceScore());
            dto.setIncidentRca(rcaDto);
        }

        return dto;
    }

}
