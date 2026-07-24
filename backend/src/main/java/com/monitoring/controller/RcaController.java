package com.monitoring.controller;

import com.monitoring.dto.IncidentRcaDto;
import com.monitoring.entity.IncidentRca;
import com.monitoring.repository.IncidentRcaRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/rca")
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:8080"})
@Tag(name = "RCA", description = "Root Cause Analysis management")
public class RcaController {
    private final IncidentRcaRepository rcaRepository;

    public RcaController(IncidentRcaRepository rcaRepository) {
        this.rcaRepository = rcaRepository;
    }

    @PostMapping
    @Operation(summary = "Create RCA", description = "Create a new root cause analysis record")
    public ResponseEntity<IncidentRcaDto> createRca(@RequestBody IncidentRcaDto dto) {
        IncidentRca rca = new IncidentRca();
        rca.setRcaId(dto.getRcaId());
        rca.setIncidentNo(dto.getIncidentNo());
        rca.setRootCause(dto.getRootCause());
        rca.setRecommendation(dto.getRecommendation());
        rca.setIncidentRefs(dto.getIncidentRefs());
        rca.setConfidenceScore(dto.getConfidenceScore());

        IncidentRca saved = rcaRepository.save(rca);

        IncidentRcaDto responseDto = new IncidentRcaDto();
        responseDto.setRcaId(saved.getRcaId());
        responseDto.setIncidentNo(saved.getIncidentNo());
        responseDto.setRootCause(saved.getRootCause());
        responseDto.setRecommendation(saved.getRecommendation());
        responseDto.setIncidentRefs(saved.getIncidentRefs());
        responseDto.setConfidenceScore(saved.getConfidenceScore());

        return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
    }

    @GetMapping("/{rcaId}")
    @Operation(summary = "Get RCA by ID", description = "Retrieve a specific RCA record")
    public ResponseEntity<IncidentRcaDto> getRca(@PathVariable String rcaId) {
        return rcaRepository.findById(rcaId)
                .map(rca -> {
                    IncidentRcaDto dto = new IncidentRcaDto();
                    dto.setRcaId(rca.getRcaId());
                    dto.setIncidentNo(rca.getIncidentNo());
                    dto.setRootCause(rca.getRootCause());
                    dto.setRecommendation(rca.getRecommendation());
                    dto.setIncidentRefs(rca.getIncidentRefs());
                    dto.setConfidenceScore(rca.getConfidenceScore());
                    return ResponseEntity.ok(dto);
                })
                .orElse(ResponseEntity.notFound().build());
    }
}
