package com.monitoring.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.auth.oauth2.AccessToken;
import com.google.auth.oauth2.GoogleCredentials;
import com.monitoring.dto.DependencyScanResult;
import com.monitoring.dto.OsvVulnerability;
import com.monitoring.dto.VertexSecurityAssessmentResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class VertexAiAssessmentService {

    private static final String CLOUD_PLATFORM_SCOPE = "https://www.googleapis.com/auth/cloud-platform";

    private final RestClient.Builder restClientBuilder;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Value("${gcp.project-id}")
    private String projectId;

    @Value("${gcp.location}")
    private String location;

    @Value("${gcp.vertex.model}")
    private String model;

    public VertexSecurityAssessmentResponse generateExecutiveAssessment(
            String applicationName,
            int critical,
            int high,
            int medium,
            int low,
            int securityScore,
            String overallRisk,
            List<DependencyScanResult> results) {

        try {
            String accessToken = getAccessToken();
            String prompt = buildPrompt(applicationName, critical, high, medium, low, securityScore, overallRisk, results);
            String endpoint = String.format(
                    "https://%s-aiplatform.googleapis.com/v1/projects/%s/locations/%s/publishers/google/models/%s:generateContent",
                    location,
                    projectId,
                    location,
                    model);

            Map<String, Object> requestBody = buildRequestBody(prompt);

            JsonNode responseNode = restClientBuilder.build()
                    .post()
                    .uri(endpoint)
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(requestBody)
                    .retrieve()
                    .body(JsonNode.class);

            String responseText = extractTextResponse(responseNode);
            if (responseText == null || responseText.isBlank()) {
                log.warn("Vertex AI returned empty response text for application {}", applicationName);
                return null;
            }

            responseText = stripCodeFence(responseText);

            return objectMapper.readValue(responseText, VertexSecurityAssessmentResponse.class);
        } catch (Exception ex) {
            log.error("Vertex AI assessment failed for application {}: {}", applicationName, ex.getMessage());
            return null;
        }
    }

    private String getAccessToken() throws IOException {
        GoogleCredentials credentials = GoogleCredentials.getApplicationDefault().createScoped(CLOUD_PLATFORM_SCOPE);
        credentials.refreshIfExpired();
        AccessToken token = credentials.getAccessToken();
        if (token == null || token.getTokenValue() == null || token.getTokenValue().isBlank()) {
            throw new IllegalStateException("Unable to obtain application default access token");
        }
        return token.getTokenValue();
    }

    private Map<String, Object> buildRequestBody(String prompt) {
        Map<String, Object> textPart = Map.of("text", prompt);
        Map<String, Object> content = Map.of(
                "role", "user",
                "parts", List.of(textPart));

        Map<String, Object> generationConfig = new HashMap<>();
        generationConfig.put("temperature", 0.2);
        generationConfig.put("responseMimeType", "application/json");

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("contents", List.of(content));
        requestBody.put("generationConfig", generationConfig);
        return requestBody;
    }

    private String buildPrompt(
            String applicationName,
            int critical,
            int high,
            int medium,
            int low,
            int securityScore,
            String overallRisk,
            List<DependencyScanResult> results) {

        StringBuilder vulnerabilityList = new StringBuilder();
        int index = 1;
        for (DependencyScanResult dependency : results) {
            for (OsvVulnerability vuln : dependency.getVulnerabilities()) {
                vulnerabilityList.append(index++)
                        .append(") library=").append(safe(dependency.getPackageName()))
                        .append(", currentVersion=").append(safe(dependency.getVersion()))
                        .append(", severity=").append(safe(vuln.getCalculatedSeverity()))
                        .append(", cvss=").append(vuln.getCvss() != null ? vuln.getCvss() : 0.0)
                        .append(", cveId=").append(safe(vuln.getId()))
                        .append(", summary=").append(safe(vuln.getSummary()))
                        .append(", fixedVersion=").append("UNKNOWN")
                        .append('\n');
            }
        }

        if (vulnerabilityList.isEmpty()) {
            vulnerabilityList.append("No vulnerabilities found in scan results.");
        }

        return "Your task is to analyze the vulnerability scan results of an application and generate an executive security assessment.\n\n"
                + "Instructions:\n"
                + "1. Analyze all vulnerabilities.\n"
                + "2. Consider the severity, CVSS score, affected libraries, and available fixed versions.\n"
                + "3. Prioritize the vulnerabilities based on business impact.\n"
                + "4. Estimate the remediation effort.\n"
                + "5. Provide a concise executive summary suitable for a management dashboard.\n"
                + "6. Do NOT hallucinate vulnerabilities.\n"
                + "7. Base your recommendations ONLY on the provided scan results.\n"
                + "8. Return ONLY valid JSON.\n"
                + "9. Do NOT include markdown.\n"
                + "10. Do NOT include explanations outside the JSON.\n\n"
                + "Application Details\n\n"
                + "Application Name:\n"
                + applicationName + "\n\n"
                + "Scan Summary\n\n"
                + "Critical Vulnerabilities: " + critical + "\n\n"
                + "High Vulnerabilities: " + high + "\n\n"
                + "Medium Vulnerabilities: " + medium + "\n\n"
                + "Low Vulnerabilities: " + low + "\n\n"
                + "Security Score: " + securityScore + "\n\n"
                + "Overall Risk: " + overallRisk + "\n\n"
                + "Vulnerability Details\n\n"
                + vulnerabilityList + "\n"
                + "Return JSON in EXACTLY this format:\n\n"
                + "{\n"
                + "  \"executiveSummary\": \"...\",\n"
                + "  \"businessImpact\": \"...\",\n"
                + "  \"estimatedRemediationTime\": \"...\",\n"
                + "  \"confidenceScore\": 92,\n"
                + "  \"overallRecommendation\": \"...\",\n"
                + "  \"prioritizedRemediation\": [\n"
                + "    {\n"
                + "      \"library\": \"...\",\n"
                + "      \"currentVersion\": \"...\",\n"
                + "      \"fixedVersion\": \"...\",\n"
                + "      \"priority\": 1,\n"
                + "      \"reason\": \"...\",\n"
                + "      \"riskReductionPercentage\": 35\n"
                + "    }\n"
                + "  ]\n"
                + "}\n\n"
                + "Rules:\n"
                + "- confidenceScore must be between 0 and 100.\n"
                + "- priority starts from 1.\n"
                + "- riskReductionPercentage must be between 0 and 100.\n"
                + "- Executive summary should not exceed 120 words.\n"
                + "- Business impact should not exceed 80 words.\n"
                + "- Overall recommendation should not exceed 120 words.\n"
                + "- Return ONLY JSON.\n"
                + "- GeneratedAt: " + LocalDateTime.now();
    }

    private String extractTextResponse(JsonNode responseNode) {
        if (responseNode == null) {
            return null;
        }

        JsonNode candidates = responseNode.path("candidates");
        if (!candidates.isArray() || candidates.isEmpty()) {
            return null;
        }

        JsonNode parts = candidates.get(0).path("content").path("parts");
        if (!parts.isArray() || parts.isEmpty()) {
            return null;
        }

        return parts.get(0).path("text").asText(null);
    }

    private String safe(String value) {
        return value == null ? "UNKNOWN" : value;
    }

    private String stripCodeFence(String text) {
        String normalized = text.trim();
        if (normalized.startsWith("```")) {
            normalized = normalized.replaceFirst("^```(?:json)?\\s*", "");
            normalized = normalized.replaceFirst("\\s*```$", "");
        }
        return normalized.trim();
    }
}
