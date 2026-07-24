package com.monitoring.scheduler;

import com.monitoring.dto.IncidentDetailsDto;
import com.monitoring.service.EmailService;
import com.monitoring.service.IncidentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class GmailSyncScheduler {
    private static final Logger log = LoggerFactory.getLogger(GmailSyncScheduler.class);

    private final EmailService emailService;
    private final IncidentService incidentService;

    @Value("${email.sync.enabled:false}")
    private boolean emailSyncEnabled;

    public GmailSyncScheduler(EmailService emailService, IncidentService incidentService) {
        this.emailService = emailService;
        this.incidentService = incidentService;
    }

    @Scheduled(fixedDelayString = "${email.sync.interval:300}000")
    public void syncEmailsWithAppSupport() {
        if (!emailSyncEnabled) {
            return;
        }

        try {
            log.info("Starting email APP SUPPORT sync...");
            List<IncidentDetailsDto> emails = emailService.fetchAppSupportEmails();

            for (IncidentDetailsDto emailIncident : emails) {
                try {
                    IncidentDetailsDto created = incidentService.createIncident(emailIncident);
                    log.info("Created incident {} from email: {}", created.getIncidentNo(), created.getDescription());
                } catch (Exception e) {
                    log.error("Error creating incident from email", e);
                }
            }

            log.info("Email sync completed. Created {} incidents from {} emails", emails.size(), emails.size());
        } catch (Exception e) {
            log.error("Error during email sync", e);
        }
    }
}
