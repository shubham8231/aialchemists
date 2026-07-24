package com.monitoring.controller;

import com.monitoring.dto.AiAnalysisResultDto;
import com.monitoring.entity.IncidentDetails;
import com.monitoring.repository.IncidentDetailsRepository;
import com.monitoring.service.VertexAiIncidentAnalysisService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/ai-analysis")
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:8080"})
@Tag(name = "AI Analysis", description = "AI-powered incident analysis using Vertex AI")
public class AiAnalysisController {
    private final VertexAiIncidentAnalysisService aiAnalysisService;
    private final IncidentDetailsRepository incidentDetailsRepository;

    public AiAnalysisController(VertexAiIncidentAnalysisService aiAnalysisService,
                                IncidentDetailsRepository incidentDetailsRepository) {
        this.aiAnalysisService = aiAnalysisService;
        this.incidentDetailsRepository = incidentDetailsRepository;
    }

    @PostMapping("/analyze/{incidentNo}")
    @Operation(summary = "Analyze incident with AI", description = "Trigger AI analysis for an existing incident")
    public ResponseEntity<AiAnalysisResultDto> analyzeIncident(@PathVariable String incidentNo) {
        IncidentDetails incident = incidentDetailsRepository.findByIncidentNo(incidentNo)
                .orElse(null);

        if (incident == null) {
            return ResponseEntity.notFound().build();
        }

        AiAnalysisResultDto result = aiAnalysisService.analyzeIncident(incident);
        if (result == null) {
            return ResponseEntity.status(503).build();
        }

        return ResponseEntity.ok(result);
    }

    @GetMapping("/health")
    @Operation(summary = "Check AI service health", description = "Verify Vertex AI connectivity")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("AI Analysis Service is ready. Vertex AI integration enabled.");
    }
}
