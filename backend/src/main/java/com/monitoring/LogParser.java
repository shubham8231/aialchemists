package com.monitoring;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.springframework.stereotype.Component;

@Component
public class LogParser {
    private static final Pattern TIMESTAMP = Pattern.compile("^(\\d{4}-\\d{2}-\\d{2}[ T]\\d{2}:\\d{2}:\\d{2}(?:[.,]\\d+)?(?:Z|[+-]\\d{2}:?\\d{2})?)");
    private static final Pattern LEVEL = Pattern.compile("\\b(TRACE|DEBUG|INFO|WARN|ERROR|FATAL)\\b");
    private static final Pattern TRACE = Pattern.compile("(?:traceId|trace_id|requestId|request_id)[=:\\s]+([A-Za-z0-9-]+)", Pattern.CASE_INSENSITIVE);
    private static final Pattern SERVICE = Pattern.compile("(?:service|app|application)[=:\\s]+([A-Za-z0-9_.-]+)", Pattern.CASE_INSENSITIVE);

    public List<LogEntry> parse(String fileName, List<String> lines) {
        List<LogEntry> result = new ArrayList<>();
        StringBuilder event = new StringBuilder();
        int startLine = 1;
        for (int i = 0; i < lines.size(); i++) {
            String line = lines.get(i);
            if (startsNewEvent(line) && !event.isEmpty()) {
                result.add(toEntry(fileName, startLine, event.toString()));
                event.setLength(0); startLine = i + 1;
            }
            if (!event.isEmpty()) event.append('\n');
            event.append(line);
        }
        if (!event.isEmpty()) result.add(toEntry(fileName, startLine, event.toString()));
        return result;
    }

    private boolean startsNewEvent(String line) { return TIMESTAMP.matcher(line).find(); }

    private LogEntry toEntry(String fileName, int line, String raw) {
        String safe = redact(raw);
        Map<String, String> meta = new LinkedHashMap<>();
        meta.put("sourceFile", fileName); meta.put("line", String.valueOf(line));
        capture(TIMESTAMP, raw, "timestamp", meta); capture(LEVEL, raw, "level", meta);
        capture(TRACE, raw, "traceId", meta); capture(SERVICE, raw, "service", meta);
        return new LogEntry(sha256(fileName + ':' + line + ':' + raw), safe, Map.copyOf(meta));
    }

    private void capture(Pattern pattern, String text, String key, Map<String, String> target) {
        Matcher m = pattern.matcher(text); if (m.find()) target.put(key, m.group(1));
    }

    /** Redact before retrieval or sending context to a model. Extend for your organisation's rules. */
    private String redact(String value) {
        return value.replaceAll("(?i)(password|secret|token|api[_-]?key|authorization)[=:\\s]+[^\\s,;]+", "$1=[REDACTED]")
                .replaceAll("\\b(?:\\d[ -]*?){13,16}\\b", "[REDACTED_CARD]");
    }

    private String sha256(String value) {
        try { return HexFormat.of().formatHex(MessageDigest.getInstance("SHA-256").digest(value.getBytes(StandardCharsets.UTF_8))); }
        catch (Exception ex) { throw new IllegalStateException(ex); }
    }
}
