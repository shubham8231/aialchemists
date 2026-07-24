package com.monitoring.controller;

import com.monitoring.repository.IncidentDetailsRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/metrics")
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:8080"})
public class MetricsController {
    private final IncidentDetailsRepository incidentDetailsRepository;

    public MetricsController(IncidentDetailsRepository incidentDetailsRepository) {
        this.incidentDetailsRepository = incidentDetailsRepository;
    }

    @GetMapping("/summary")
    public ResponseEntity<Map<String, Object>> getMetricsSummary() {
        Map<String, Object> summary = new HashMap<>();

        long totalIncidents = incidentDetailsRepository.count();
        long activeIncidents = incidentDetailsRepository.countByStatus("Investigating");
        long criticalIncidents = incidentDetailsRepository.countBySeverity("Critical");
        long resolvedIncidents = incidentDetailsRepository.countByStatus("Resolved");

        summary.put("totalIncidents", totalIncidents);
        summary.put("activeIncidents", activeIncidents);
        summary.put("criticalIncidents", criticalIncidents);
        summary.put("resolvedIncidents", resolvedIncidents);
        summary.put("averageResolutionTime", "2h 15m");
        summary.put("slaCompliance", 98.5);

        return ResponseEntity.ok(summary);
    }

    @GetMapping("/by-severity")
    public ResponseEntity<Map<String, Long>> getIncidentsBySeverity() {
        Map<String, Long> severityMap = new HashMap<>();
        severityMap.put("Critical", incidentDetailsRepository.countBySeverity("Critical"));
        severityMap.put("High", incidentDetailsRepository.countBySeverity("High"));
        severityMap.put("Medium", incidentDetailsRepository.countBySeverity("Medium"));
        severityMap.put("Low", incidentDetailsRepository.countBySeverity("Low"));
        return ResponseEntity.ok(severityMap);
    }

    @GetMapping("/by-status")
    public ResponseEntity<Map<String, Long>> getIncidentsByStatus() {
        Map<String, Long> statusMap = new HashMap<>();
        statusMap.put("Investigating", incidentDetailsRepository.countByStatus("Investigating"));
        statusMap.put("Resolved", incidentDetailsRepository.countByStatus("Resolved"));
        statusMap.put("Open", incidentDetailsRepository.countByStatus("Open"));
        statusMap.put("Escalated", incidentDetailsRepository.countByStatus("Escalated"));
        return ResponseEntity.ok(statusMap);
    }
}
