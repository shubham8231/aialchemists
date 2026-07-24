package com.monitoring;

import java.util.Map;

public record LogEntry(String id, String text, Map<String, String> metadata) { }
