package com.monitoring;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.net.URI;
import java.net.http.*;
import java.time.Duration;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class OpenAiGenerator {
    private final String apiKey, model;
    private final ObjectMapper mapper;
    private final HttpClient client = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(15)).build();
    OpenAiGenerator(@Value("${log-rag.openai.api-key:}") String apiKey, @Value("${log-rag.openai.model:gpt-4.1-mini}") String model, ObjectMapper mapper) {
        this.apiKey = apiKey; this.model = model; this.mapper = mapper;
    }
    public boolean enabled() { return apiKey != null && !apiKey.isBlank(); }
    public String answer(String question, List<LogIndex.SearchHit> hits) {
        if (!enabled()) return null;
        try {
            String context = hits.stream().map(h -> "SOURCE " + h.entry().metadata() + "\n" + h.entry().text())
                .reduce("", (a, b) -> a + "\n---\n" + b);
            var body = mapper.createObjectNode(); body.put("model", model); body.put("temperature", 0);
            var messages = body.putArray("messages");
            messages.addObject().put("role", "system").put("content", "Answer only from supplied logs. State uncertainty. Cite sourceFile, line and traceId when present. Never invent events.");
            messages.addObject().put("role", "user").put("content", "Question: " + question + "\n\nLogs:\n" + context);
            HttpRequest request = HttpRequest.newBuilder(URI.create("https://api.openai.com/v1/chat/completions"))
                .header("Authorization", "Bearer " + apiKey).header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(mapper.writeValueAsString(body))).timeout(Duration.ofSeconds(60)).build();
            JsonNode response = mapper.readTree(client.send(request, HttpResponse.BodyHandlers.ofString()).body());
            return response.at("/choices/0/message/content").asText(null);
        } catch (Exception ex) { return "Generation failed; retrieved log evidence is returned below. Cause: " + ex.getMessage(); }
    }
}
