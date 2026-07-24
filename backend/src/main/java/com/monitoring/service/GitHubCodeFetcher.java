package com.monitoring.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.util.*;

@Service
public class GitHubCodeFetcher {
    private static final Logger logger = LoggerFactory.getLogger(GitHubCodeFetcher.class);
    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${github.repo-owner:anthropics}")
    private String repoOwner;

    @Value("${github.repo-name:claude-code}")
    private String repoName;

    @Value("${github.api-token:#{null}}")
    private String githubToken;

    public String fetchRelevantCode(String errorType, String applicationName) {
        try {
            logger.info("Fetching GitHub code context for error type: {}, app: {}", errorType, applicationName);

            List<String> searchPatterns = buildSearchPatterns(errorType, applicationName);
            StringBuilder codeContext = new StringBuilder("GitHub Code Context:\n");

            for (String pattern : searchPatterns) {
                String code = searchFiles(pattern);
                if (!code.isEmpty()) {
                    codeContext.append(code).append("\n");
                }
            }

            return codeContext.toString();
        } catch (Exception e) {
            logger.warn("Error fetching GitHub code: {}", e.getMessage());
            return "GitHub code context unavailable.";
        }
    }

    private List<String> buildSearchPatterns(String errorType, String applicationName) {
        List<String> patterns = new ArrayList<>();

        switch (errorType.toUpperCase()) {
            case "DB":
                patterns.add("database");
                patterns.add("connection");
                patterns.add("sql");
                break;
            case "NPE":
                patterns.add("nullpointer");
                patterns.add("null");
                break;
            case "TIMEOUT":
                patterns.add("timeout");
                patterns.add("delay");
                break;
            case "CONNECTION":
                patterns.add("network");
                patterns.add("connection");
                break;
            default:
                patterns.add("error");
                patterns.add("exception");
        }

        if (applicationName != null && !applicationName.isEmpty()) {
            patterns.add(applicationName.toLowerCase().replace(" ", "-"));
        }

        return patterns;
    }

    private String searchFiles(String pattern) {
        try {
            // Note: Requires GitHub API integration
            // For now, returning placeholder
            // Full implementation would require GitHub API token and proper file search
            return "";
        } catch (Exception e) {
            logger.debug("Error searching files for pattern: {}", pattern, e);
            return "";
        }
    }

    public void setRepoOwner(String repoOwner) {
        this.repoOwner = repoOwner;
    }

    public void setRepoName(String repoName) {
        this.repoName = repoName;
    }
}
