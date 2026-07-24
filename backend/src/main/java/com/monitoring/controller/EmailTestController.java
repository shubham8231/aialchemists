package com.monitoring.controller;

import com.monitoring.dto.IncidentDetailsDto;
import com.monitoring.service.IncidentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/email-test")
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:8080"})
public class EmailTestController {
    private final IncidentService incidentService;

    public EmailTestController(IncidentService incidentService) {
        this.incidentService = incidentService;
    }

    @PostMapping("/simulate-app-support-email")
    public ResponseEntity<Map<String, Object>> simulateAppSupportEmail(
            @RequestParam(defaultValue = "Database Connection Error") String appName,
            @RequestParam(defaultValue = "APP SUPPORT: Database Connection Error") String subject,
            @RequestParam(defaultValue = "URGENT: Database connection pool exhausted in production! Max connections reached.") String emailBody) {

        Map<String, Object> response = new HashMap<>();

        try {
            // Create incident from simulated email data
            IncidentDetailsDto incident = new IncidentDetailsDto();
            incident.setApplicationName(appName);
            incident.setEnvironment("EMAIL");
            incident.setDescription(subject);
            incident.setSeverity("High");
            incident.setStatus("New");
            incident.setErrorType("EMAIL_ALERT");
            incident.setRawLog(emailBody.substring(0, Math.min(500, emailBody.length())));
            incident.setAiSummary("Email-based alert: " + subject);
            incident.setCreatedBy("test@company.com");

            // Create incident using existing service
            IncidentDetailsDto created = incidentService.createIncident(incident);

            response.put("status", "success");
            response.put("message", "Test email processed successfully");
            response.put("incidentNo", created.getIncidentNo());
            response.put("applicationName", created.getApplicationName());
            response.put("severity", created.getSeverity());
            response.put("errorType", created.getErrorType());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("status", "error");
            response.put("message", e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }

    @GetMapping("/sample-app-support")
    public ResponseEntity<Map<String, Object>> getSampleEmail() {
        Map<String, Object> sample = new HashMap<>();
        sample.put("from", "db-alerts@company.com");
        sample.put("subject", "APP SUPPORT: Database Connection Error");
        sample.put("body", "URGENT: Database connection pool exhausted in production!\n\n" +
                "Details:\n" +
                "- Service: User Authentication Service\n" +
                "- Environment: PRODUCTION\n" +
                "- Time: 2026-07-24 01:38:00\n" +
                "- Error: Connection pool limit (50) reached\n\n" +
                "Impact:\n" +
                "- 50% of login requests failing\n" +
                "- Average response time: 8000ms\n" +
                "- Affected users: 10,000+\n\n" +
                "Recommended Action:\n" +
                "1. Increase connection pool size\n" +
                "2. Check for connection leaks\n" +
                "3. Restart database service if necessary");

        return ResponseEntity.ok(sample);
    }
}
