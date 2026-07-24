package com.monitoring.controller;

import com.monitoring.dto.IncidentDetailsDto;
import com.monitoring.service.EmailService;
import com.monitoring.service.IncidentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/email")
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:8080"})
public class GmailController {
    private static final Logger log = LoggerFactory.getLogger(GmailController.class);

    private final EmailService emailService;
    private final IncidentService incidentService;

    public GmailController(EmailService emailService, IncidentService incidentService) {
        this.emailService = emailService;
        this.incidentService = incidentService;
    }

    @GetMapping("/sync-app-support")
    public ResponseEntity<Map<String, Object>> syncAppSupportEmails() {
        Map<String, Object> response = new HashMap<>();
        try {
            List<IncidentDetailsDto> emails = emailService.fetchAppSupportEmails();
            int created = 0;

            for (IncidentDetailsDto emailIncident : emails) {
                try {
                    IncidentDetailsDto createdIncident = incidentService.createIncident(emailIncident);
                    created++;
                    log.info("Created incident from email: {}", createdIncident.getIncidentNo());
                } catch (Exception e) {
                    log.error("Error creating incident from email", e);
                }
            }

            response.put("status", "success");
            response.put("totalEmails", emails.size());
            response.put("incidentsCreated", created);
            response.put("message", created + " incidents created from " + emails.size() + " APP SUPPORT emails");

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error syncing emails", e);
            response.put("status", "error");
            response.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @GetMapping("/test-connection")
    public ResponseEntity<Map<String, Object>> testConnection() {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "success");
        response.put("message", "Email service ready. Configure email.username and email.password to enable sync");
        return ResponseEntity.ok(response);
    }
}
