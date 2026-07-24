package com.monitoring;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import java.time.LocalDateTime;
import java.util.*;

@SpringBootApplication
@RestController
@EnableScheduling
public class MonitoringApplication {

    private List<Map<String, Object>> metrics = new ArrayList<>();
    private List<Map<String, Object>> logs = new ArrayList<>();
    private List<Map<String, Object>> alerts = new ArrayList<>();
    private Map<String, Object> health = new HashMap<>();

    public static void main(String[] args) {
        SpringApplication.run(MonitoringApplication.class, args);
    }

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**")
                        .allowedOrigins("http://localhost:3000", "http://localhost:8080")
                        .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                        .allowedHeaders("*")
                        .allowCredentials(true)
                        .maxAge(3600);
            }
        };
    }

    // Metrics Endpoints
    @PostMapping("/metrics")
    public Map<String, Object> createMetric(@RequestBody Map<String, Object> metric) {
        metric.put("id", System.currentTimeMillis());
        metric.put("timestamp", LocalDateTime.now().toString());
        metrics.add(metric);
        return metric;
    }

    @GetMapping("/metrics/application/{appName}")
    public Map<String, Object> getMetrics() {
        Map<String, Object> response = new HashMap<>();
        response.put("content", metrics);
        response.put("totalElements", metrics.size());
        response.put("totalPages", 1);
        return response;
    }

    // Logs Endpoints
    @PostMapping("/logs")
    public Map<String, Object> createLog(@RequestBody Map<String, Object> log) {
        log.put("id", System.currentTimeMillis());
        log.put("timestamp", LocalDateTime.now().toString());
        logs.add(log);
        return log;
    }

    @GetMapping("/logs/application/{appName}")
    public Map<String, Object> getLogs() {
        Map<String, Object> response = new HashMap<>();
        response.put("content", logs);
        response.put("totalElements", logs.size());
        response.put("totalPages", 1);
        return response;
    }

    // Health Endpoints
    @PostMapping("/health")
    public Map<String, Object> recordHealth(@RequestBody Map<String, Object> healthData) {
        health = new HashMap<>(healthData);
        health.put("id", 1);
        health.put("timestamp", LocalDateTime.now().toString());
        return health;
    }

    @GetMapping("/health/application/{appName}")
    public Map<String, Object> getHealth() {
        if (health.isEmpty()) {
            health.put("status", "UP");
            health.put("responseTimeMs", 150);
            health.put("timestamp", LocalDateTime.now().toString());
        }
        return health;
    }

    @GetMapping("/health/all")
    public List<Map<String, Object>> getAllHealth() {
        return Arrays.asList(health.isEmpty() ? createDefaultHealth() : health);
    }

    private Map<String, Object> createDefaultHealth() {
        Map<String, Object> h = new HashMap<>();
        h.put("status", "UP");
        h.put("applicationName", "sample-app");
        h.put("responseTimeMs", 150);
        h.put("timestamp", LocalDateTime.now().toString());
        return h;
    }

    // Alerts Endpoints
    @PostMapping("/alerts")
    public Map<String, Object> createAlert(@RequestBody Map<String, Object> alert) {
        alert.put("id", System.currentTimeMillis());
        alert.put("status", "ACTIVE");
        alert.put("triggeredAt", LocalDateTime.now().toString());
        alerts.add(alert);
        return alert;
    }

    @GetMapping("/alerts/application/{appName}")
    public Map<String, Object> getAlerts() {
        Map<String, Object> response = new HashMap<>();
        response.put("content", alerts);
        response.put("totalElements", alerts.size());
        response.put("totalPages", 1);
        return response;
    }

    @GetMapping("/alerts/application/{appName}/active")
    public List<Map<String, Object>> getActiveAlerts() {
        return alerts;
    }

    // Health Check
    @GetMapping("/management/health")
    public Map<String, String> status() {
        return Collections.singletonMap("status", "UP");
    }

    @GetMapping("/")
    public Map<String, Object> root() {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "running");
        response.put("message", "Application Monitoring System is up!");
        response.put("version", "1.0.0");
        response.put("timestamp", LocalDateTime.now().toString());
        return response;
    }
}
