package com.monitoring.controllers;

import com.monitoring.LogIndex;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
@Tag(name = "System", description = "System health and information endpoints")
public class HealthController {

    private final LogIndex logIndex;
    private final long startTime = System.currentTimeMillis();

    HealthController(LogIndex logIndex) {
        this.logIndex = logIndex;
    }

    @GetMapping("/health")
    @Operation(
        summary = "Get system health status",
        description = "Returns overall health status including indexed entries count",
        tags = {"System"}
    )
    @ApiResponse(responseCode = "200", description = "System is operational")
    public Map<String, Object> health() {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "UP");
        response.put("service", "rag-agent");
        response.put("version", "0.0.1");
        response.put("timestamp", Instant.now().toString());
        response.put("uptime_ms", System.currentTimeMillis() - startTime);
        response.put("indexed_entries", logIndex.size());
        return response;
    }

    @GetMapping("/info")
    @Operation(
        summary = "Get system information",
        description = "Returns detailed information about the RAG agent including features and available endpoints",
        tags = {"System"}
    )
    @ApiResponse(responseCode = "200", description = "System information retrieved")
    public Map<String, Object> info() {
        Map<String, Object> response = new HashMap<>();
        response.put("name", "RAG-Based Log Knowledge Base");
        response.put("description", "Retrieval-Augmented Generation system for analyzing log files with AI assistance");
        response.put("version", "0.0.1-SNAPSHOT");
        response.put("features", new String[]{
            "Log ingestion from files",
            "Smart metadata extraction",
            "Full-text search with TF-IDF scoring",
            "Data redaction for sensitive information",
            "AI-assisted answer generation via OpenAI",
            "Directory watching for automatic indexing",
            "Support for multiple error types",
            "Conversational RAG chatbot interface",
            "Swagger/OpenAPI documentation",
            "Incident database analysis",
            "RCA tracking and analysis"
        });
        response.put("endpoints", new String[]{
            "POST /api/logs - Upload log file",
            "POST /api/query - Query indexed logs",
            "POST /api/samples/* - Ingest sample errors",
            "POST /api/chat/ask - Ask RAG chatbot (conversational)",
            "GET /api/chat/ask - Ask chatbot (GET method)",
            "GET /api/chat/health - Chatbot service health",
            "GET /api/incident-analysis/error-types - Get all error types",
            "GET /api/incident-analysis/analyze/{incidentNo} - Analyze incident",
            "GET /api/health - System health status",
            "GET /api/info - Service information",
            "GET /swagger-ui.html - Interactive API documentation"
        });
        response.put("indexed_entries", logIndex.size());
        return response;
    }
}
