package com.monitoring.scheduler;

import com.monitoring.dto.IncidentDetailsDto;
import com.monitoring.dto.IncidentRcaDto;
import com.monitoring.service.IncidentService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import java.io.*;
import java.nio.file.*;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class LogFileMonitor {
    private final IncidentService incidentService;

    @Value("${app.log.file.path:./logs/application.log}")
    private String logFilePath;

    @Value("${app.log.check.interval.seconds:40}")
    private int checkIntervalSeconds;

    private long lastProcessedPosition = 0;

    public LogFileMonitor(IncidentService incidentService) {
        this.incidentService = incidentService;
    }

    @Scheduled(fixedRate = 30000) // 30 seconds
    public void monitorLogFile() {
        try {
            File logFile = new File(logFilePath);
            if (!logFile.exists()) {
                System.out.println("Log file not found at: " + logFilePath);
                return;
            }

            List<String> errorLogs = readErrorLogsFromLastSeconds(logFile);
            for (String errorLog : errorLogs) {
                processErrorLog(errorLog);
            }
        } catch (Exception e) {
            System.err.println("Error monitoring log file: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private List<String> readErrorLogsFromLastSeconds(File logFile) throws IOException {
        List<String> errorLogs = new ArrayList<>();
        long fileSize = logFile.length();
        long checkDuration = checkIntervalSeconds * 1000; // Convert to milliseconds
        long now = System.currentTimeMillis();
        long checkFrom = now - checkDuration;

        try (RandomAccessFile raf = new RandomAccessFile(logFile, "r")) {
            if (lastProcessedPosition > fileSize) {
                lastProcessedPosition = 0; // Reset if file was rotated
            }

            raf.seek(lastProcessedPosition);
            String line;
            StringBuilder errorBlock = new StringBuilder();
            boolean inErrorBlock = false;

            while ((line = raf.readLine()) != null) {
                if (isErrorLog(line)) {
                    if (inErrorBlock) {
                        errorLogs.add(errorBlock.toString());
                    }
                    errorBlock = new StringBuilder(line);
                    inErrorBlock = true;
                } else if (inErrorBlock) {
                    errorBlock.append("\n").append(line);
                }
            }

            if (inErrorBlock) {
                errorLogs.add(errorBlock.toString());
            }

            lastProcessedPosition = raf.getFilePointer();
        }

        return errorLogs;
    }

    private boolean isErrorLog(String line) {
        return line.contains("ERROR") || line.contains("Exception") ||
               line.contains("FATAL") || line.contains("error");
    }

    private void processErrorLog(String logContent) {
        try {
            String[] lines = logContent.split("\n");
            String firstLine = lines[0];

            String applicationName = extractApplicationName(firstLine);
            String errorType = extractErrorType(logContent);
            String description = extractDescription(firstLine);
            String environment = extractEnvironment(firstLine);
            String severity = determineSeverity(logContent);

            // Create RCA
            IncidentRcaDto rcaDto = new IncidentRcaDto();
            rcaDto.setRootCause(extractRootCause(logContent));
            rcaDto.setRecommendation(generateRecommendation(errorType));
            rcaDto.setConfidenceScore(calculateConfidenceScore(logContent));

            // Create Incident
            IncidentDetailsDto incidentDto = new IncidentDetailsDto();
            incidentDto.setApplicationName(applicationName);
            incidentDto.setEnvironment(environment != null ? environment : "UNKNOWN");
            incidentDto.setDescription(description);
            incidentDto.setSeverity(severity);
            incidentDto.setStatus("New");
            incidentDto.setErrorType(errorType);
            incidentDto.setRawLog(logContent);
            incidentDto.setAiSummary(generateAiSummary(logContent));
            incidentDto.setIncidentRca(rcaDto);
            incidentDto.setCreatedBy("LOG_MONITOR");

            incidentService.createIncident(incidentDto);
            System.out.println("Created incident from error log: " + applicationName);
        } catch (Exception e) {
            System.err.println("Error processing error log: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private String extractApplicationName(String line) {
        // Extract from log format: [APP_NAME] or just use default
        Pattern pattern = Pattern.compile("\\[(.*?)\\]");
        Matcher matcher = pattern.matcher(line);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return "unknown-app";
    }

    private String extractErrorType(String content) {
        if (content.contains("NullPointerException")) return "NPE";
        if (content.contains("SQLException") || content.contains("Database")) return "DB";
        if (content.contains("LDAP")) return "LDAP";
        if (content.contains("Connection")) return "CONNECTION";
        if (content.contains("Timeout")) return "TIMEOUT";
        return "GENERAL";
    }

    private String extractDescription(String line) {
        // Extract meaningful part of error message
        if (line.contains("ERROR")) {
            String[] parts = line.split("ERROR");
            return parts.length > 1 ? parts[1].substring(0, Math.min(200, parts[1].length())) : line;
        }
        return line.substring(0, Math.min(200, line.length()));
    }

    private String extractEnvironment(String line) {
        if (line.contains("PROD")) return "PROD";
        if (line.contains("UAT")) return "UAT";
        if (line.contains("DEV")) return "DEV";
        return "UNKNOWN";
    }

    private String determineSeverity(String content) {
        if (content.contains("FATAL") || content.contains("NullPointerException")) return "Critical";
        if (content.contains("ERROR") || content.contains("Exception")) return "High";
        return "Medium";
    }

    private String extractRootCause(String content) {
        String[] lines = content.split("\n");
        for (String line : lines) {
            if (line.contains("Caused by") || line.contains("Exception")) {
                return line.trim();
            }
        }
        return lines.length > 0 ? lines[0] : "Unknown cause";
    }

    private String generateRecommendation(String errorType) {
        switch (errorType) {
            case "DB":
                return "Check database connectivity, verify credentials, and check database server status";
            case "LDAP":
                return "Verify LDAP configuration, check LDAP server connectivity and credentials";
            case "NPE":
                return "Review null pointer handling, add null checks, and verify object initialization";
            case "CONNECTION":
                return "Check network connectivity, firewall rules, and service endpoint availability";
            default:
                return "Review application logs and error stack trace for more details";
        }
    }

    private Double calculateConfidenceScore(String content) {
        // Simple confidence calculation based on error information completeness
        double score = 50.0;
        if (content.contains("Caused by")) score += 20;
        if (content.contains("at ")) score += 15;
        if (content.contains("Exception")) score += 10;
        return Math.min(score, 100.0);
    }

    private String generateAiSummary(String content) {
        String[] lines = content.split("\n");
        StringBuilder summary = new StringBuilder();
        for (int i = 0; i < Math.min(3, lines.length); i++) {
            summary.append(lines[i]).append(" ");
        }
        return summary.toString().trim();
    }
}
