package com.monitoring.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.auth.oauth2.GoogleCredentials;
import com.monitoring.config.VertexAiConfig;
import com.monitoring.dto.AiAnalysisResultDto;
import com.monitoring.entity.IncidentDetails;
import com.monitoring.repository.IncidentDetailsRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class VertexAiIncidentAnalysisService {
    private static final Logger logger = LoggerFactory.getLogger(VertexAiIncidentAnalysisService.class);
    private final VertexAiConfig vertexAiConfig;
    private final IncidentDetailsRepository incidentDetailsRepository;
    private final AuditLogService auditLogService;

    public VertexAiIncidentAnalysisService(VertexAiConfig vertexAiConfig,
                                           IncidentDetailsRepository incidentDetailsRepository,
                                           AuditLogService auditLogService) {
        this.vertexAiConfig = vertexAiConfig;
        this.incidentDetailsRepository = incidentDetailsRepository;
        this.auditLogService = auditLogService;
    }

    public AiAnalysisResultDto analyzeIncident(IncidentDetails incident) {
        if (!vertexAiConfig.isEnabled()) {
            return null;
        }

        long startTime = System.currentTimeMillis();
        String incidentNo = incident.getIncidentNo();

        try {
            logger.info("Starting AI analysis for incident: {}", incidentNo);
            auditLogService.logRcaStarted(incidentNo);

            auditLogService.logFindingSimilarIncidents(incidentNo);
            String similarIncidents = findSimilarIncidents(incident);

            String prompt = buildAnalysisPrompt(incident, similarIncidents);

            auditLogService.logCallingVertexAi(incidentNo);
            AiAnalysisResultDto result = callVertexAi(prompt);

            auditLogService.logParsingResponse(incidentNo);
            result.setRelatedIncidents(similarIncidents);

            long duration = System.currentTimeMillis() - startTime;
            auditLogService.logRcaCompleted(incidentNo, incidentNo + "-RCA", duration);
            logger.info("AI analysis completed for incident: {} in {}ms", incidentNo, duration);
            return result;
        } catch (Exception e) {
            long duration = System.currentTimeMillis() - startTime;
            auditLogService.logRcaFailed(incidentNo, e.getMessage(), duration);
            logger.error("Error analyzing incident with Vertex AI: {}", incidentNo, e);
            return null;
        }
    }

    private String findSimilarIncidents(IncidentDetails incident) {
        try {
            List<IncidentDetails> similar = incidentDetailsRepository
                    .findBySeverity(incident.getSeverity())
                    .stream()
                    .limit(5)
                    .collect(Collectors.toList());

            if (similar.isEmpty()) {
                return "No similar incidents found in database.";
            }

            StringBuilder sb = new StringBuilder("Similar resolved incidents:\n");
            for (IncidentDetails inc : similar) {
                if ("Resolved".equalsIgnoreCase(inc.getStatus())) {
                    sb.append("- ").append(inc.getIncidentNo())
                            .append(" (").append(inc.getApplicationName()).append("): ")
                            .append(inc.getAiSummary()).append("\n");
                }
            }
            return sb.toString();
        } catch (Exception e) {
            logger.warn("Error finding similar incidents", e);
            return "Unable to fetch similar incidents.";
        }
    }

    private String buildAnalysisPrompt(IncidentDetails incident, String similarIncidents) {
        return String.format(
                "You are an expert incident response engineer. Analyze the following production incident and provide root cause analysis.\n\n" +
                "INCIDENT DETAILS:\n" +
                "Incident Number: %s\n" +
                "Application: %s\n" +
                "Environment: %s\n" +
                "Severity: %s\n" +
                "Error Type: %s\n" +
                "Description: %s\n\n" +
                "ERROR LOG:\n%s\n\n" +
                "SIMILAR INCIDENTS (from history):\n%s\n\n" +
                "Please provide:\n" +
                "1. ROOT_CAUSE: Technical root cause analysis (2-3 sentences)\n" +
                "2. RECOMMENDATION: Actionable solution (2-3 sentences)\n" +
                "3. CONFIDENCE: Your confidence in this analysis (0-100)\n\n" +
                "Format your response as:\n" +
                "ROOT_CAUSE: [your analysis]\n" +
                "RECOMMENDATION: [your recommendation]\n" +
                "CONFIDENCE: [0-100]",
                incident.getIncidentNo(),
                incident.getApplicationName(),
                incident.getEnvironment(),
                incident.getSeverity(),
                incident.getErrorType(),
                incident.getDescription(),
                incident.getRawLog() != null ? incident.getRawLog() : "No detailed logs available",
                similarIncidents
        );
    }

    private AiAnalysisResultDto callVertexAi(String prompt) throws Exception {
        String url = String.format(
            "https://us-central1-aiplatform.googleapis.com/v1/projects/%s/locations/%s/publishers/google/models/%s:generateContent",
            vertexAiConfig.getProjectId(),
            vertexAiConfig.getLocation(),
            vertexAiConfig.getModel()
        );

        // Get credentials and refresh token
        GoogleCredentials credentials = GoogleCredentials.getApplicationDefault();
        credentials.refreshIfExpired();
        String token = credentials.getAccessToken().getTokenValue();

        RestTemplate restTemplate = new RestTemplate();

        String requestBody = String.format("""
            {
              "contents": [{
                "role": "user",
                "parts": [{
                  "text": "%s"
                }]
              }]
            }""", prompt.replace("\"", "\\\""));

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(token);

        HttpEntity<String> request = new HttpEntity<>(requestBody, headers);

        try {
            ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class);
            return parseVertexAiRestResponse(response.getBody());
        } catch (Exception e) {
            logger.error("Error calling Vertex AI API: {}", e.getMessage());
            throw e;
        }
    }

    private AiAnalysisResultDto parseVertexAiRestResponse(String responseBody) {
        AiAnalysisResultDto result = new AiAnalysisResultDto();

        try {
            com.fasterxml.jackson.databind.JsonNode root = new com.fasterxml.jackson.databind.ObjectMapper().readTree(responseBody);

            if (root.has("candidates") && root.get("candidates").isArray()) {
                com.fasterxml.jackson.databind.JsonNode firstCandidate = root.get("candidates").get(0);
                if (firstCandidate.has("content") && firstCandidate.get("content").has("parts")) {
                    com.fasterxml.jackson.databind.JsonNode firstPart = firstCandidate.get("content").get("parts").get(0);
                    if (firstPart.has("text")) {
                        String content = firstPart.get("text").asText();
                        extractAnalysisData(content, result);
                    }
                }
            }
        } catch (Exception e) {
            logger.error("Error parsing Vertex AI REST response", e);
        }

        return result;
    }

    private void extractAnalysisData(String content, AiAnalysisResultDto result) {
        String rootCause = extractSection(content, "ROOT_CAUSE:");
        String recommendation = extractSection(content, "RECOMMENDATION:");
        String confidenceStr = extractSection(content, "CONFIDENCE:");

        result.setRootCause(rootCause);
        result.setRecommendation(recommendation);
        result.setAnalysis(content);

        try {
            Double confidence = Double.parseDouble(confidenceStr.replaceAll("[^0-9.]", ""));
            result.setConfidenceScore(Math.min(confidence, 100.0));
        } catch (Exception e) {
            result.setConfidenceScore(75.0);
        }
    }

    private String extractSection(String content, String prefix) {
        try {
            int startIdx = content.indexOf(prefix);
            if (startIdx == -1) return "";

            startIdx += prefix.length();
            int endIdx = content.indexOf("\n", startIdx);
            if (endIdx == -1) endIdx = content.length();

            return content.substring(startIdx, endIdx).trim();
        } catch (Exception e) {
            return "";
        }
    }
}
