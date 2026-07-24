package com.monitoring.service;

import com.monitoring.dto.IncidentDetailsDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.mail.*;
import java.util.*;

@Service
public class EmailService {
    private static final Logger log = LoggerFactory.getLogger(EmailService.class);

    @Value("${email.imap.host:imap.gmail.com}")
    private String imapHost;

    @Value("${email.imap.port:993}")
    private int imapPort;

    @Value("${email.username:#{null}}")
    private String emailUsername;

    @Value("${email.password:#{null}}")
    private String emailPassword;

    public List<IncidentDetailsDto> fetchAppSupportEmails() {
        List<IncidentDetailsDto> incidents = new ArrayList<>();

        if (emailUsername == null || emailUsername.isEmpty() || emailPassword == null || emailPassword.isEmpty()) {
            log.warn("Email credentials not configured. Skipping email fetch.");
            return incidents;
        }

        try {
            Properties props = new Properties();
            props.put("mail.imap.host", imapHost);
            props.put("mail.imap.port", imapPort);
            props.put("mail.imap.socketFactory.port", imapPort);
            props.put("mail.imap.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
            props.put("mail.imap.socketFactory.fallback", "false");
            props.put("mail.imap.starttls.enable", "true");

            Session session = Session.getInstance(props, new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(emailUsername, emailPassword);
                }
            });

            Store store = session.getStore("imap");
            store.connect(imapHost, imapPort, emailUsername, emailPassword);
            Folder inbox = store.getFolder("INBOX");
            inbox.open(Folder.READ_ONLY);

            Message[] messages = inbox.getMessages();
            int appSupportCount = 0;

            for (Message message : messages) {
                try {
                    String subject = message.getSubject();
                    if (subject != null && subject.toUpperCase().contains("APP SUPPORT")) {
                        IncidentDetailsDto incident = parseEmailToIncident(message);
                        if (incident != null) {
                            incidents.add(incident);
                            appSupportCount++;
                        }
                    }
                } catch (Exception e) {
                    log.error("Error processing email", e);
                }
            }

            log.info("Found {} APP SUPPORT emails out of {}", appSupportCount, messages.length);
            inbox.close(false);
            store.close();
        } catch (Exception e) {
            log.error("Error fetching emails from IMAP", e);
        }

        return incidents;
    }

    private IncidentDetailsDto parseEmailToIncident(Message message) {
        try {
            String subject = message.getSubject();
            String from = message.getFrom() != null && message.getFrom().length > 0 ? message.getFrom()[0].toString() : "Unknown";
            String content = getEmailContent(message);

            IncidentDetailsDto incident = new IncidentDetailsDto();
            incident.setApplicationName(extractApplicationName(subject));
            incident.setEnvironment("EMAIL");
            incident.setDescription(subject);
            incident.setSeverity("High");
            incident.setStatus("New");
            incident.setErrorType("EMAIL_ALERT");
            incident.setRawLog(content.substring(0, Math.min(500, content.length())));
            incident.setAiSummary("Email-based alert: " + subject);
            incident.setCreatedBy(from);

            log.info("Created incident from email: {}", subject);
            return incident;
        } catch (Exception e) {
            log.error("Error parsing email", e);
            return null;
        }
    }

    private String getEmailContent(Message message) {
        StringBuilder content = new StringBuilder();
        try {
            Object obj = message.getContent();
            if (obj instanceof String) {
                content.append((String) obj);
            } else if (obj instanceof Multipart) {
                Multipart multipart = (Multipart) obj;
                for (int i = 0; i < multipart.getCount(); i++) {
                    BodyPart bodyPart = multipart.getBodyPart(i);
                    if (bodyPart.isMimeType("text/plain")) {
                        Object partContent = bodyPart.getContent();
                        if (partContent != null) {
                            content.append(partContent.toString());
                        }
                    }
                }
            }
        } catch (Exception e) {
            log.error("Error extracting email content", e);
            content.append("[Unable to extract content]");
        }
        return content.toString();
    }

    private String extractApplicationName(String subject) {
        if (subject != null && subject.contains("-")) {
            String[] parts = subject.split("-");
            if (parts.length > 1) {
                return parts[1].trim();
            }
        }
        return "Unknown-App";
    }
}
