package com.monitoring;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

@Service
public class LogIndex {
    private final Map<String, LogEntry> entries = new ConcurrentHashMap<>();

    public int addAll(Collection<LogEntry> additions) {
        int before = entries.size(); additions.forEach(e -> entries.putIfAbsent(e.id(), e)); return entries.size() - before;
    }
    public int size() { return entries.size(); }

    public List<SearchHit> search(String question, String service, String level, int limit) {
        Set<String> query = tokens(question);
        return entries.values().stream()
            .filter(e -> matches(e, "service", service) && matches(e, "level", level))
            .map(e -> new SearchHit(e, score(query, tokens(e.text()))))
            .filter(hit -> hit.score() > 0)
            .sorted(Comparator.comparingDouble(SearchHit::score).reversed())
            .limit(Math.clamp(limit, 1, 20)).toList();
    }
    private boolean matches(LogEntry e, String field, String expected) {
        return expected == null || expected.isBlank() || expected.equalsIgnoreCase(e.metadata().get(field));
    }
    private double score(Set<String> query, Set<String> document) {
        long overlap = query.stream().filter(document::contains).count();
        return query.isEmpty() ? 0 : (double) overlap / query.size();
    }
    private Set<String> tokens(String text) {
        return Arrays.stream(text.toLowerCase(Locale.ROOT).split("[^a-z0-9_.-]+"))
            .filter(s -> s.length() > 1).collect(Collectors.toSet());
    }
    public record SearchHit(LogEntry entry, double score) { }
}
