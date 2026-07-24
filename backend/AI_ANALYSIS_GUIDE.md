# AI-Powered Incident Analysis System

## Overview

The Application Monitoring & Incident Investigation System now integrates **Google Vertex AI** for intelligent incident analysis using Gemini models. When incidents are created, the system automatically analyzes error logs, searches historical incidents, and generates AI-powered recommendations with root cause analysis.

## Architecture

### Components

1. **VertexAiIncidentAnalysisService** (`com.monitoring.service`)
   - Orchestrates AI analysis workflow
   - Calls Vertex AI Gemini API
   - Searches historical incidents for context
   - Parses AI responses into structured RCA data

2. **VertexAiConfig** (`com.monitoring.config`)
   - Configuration properties for Vertex AI
   - Project ID: `hack-team-aialchemists-2026`
   - Location: `us-central1`
   - Model: `gemini-1.5-pro`

3. **GitHubCodeFetcher** (`com.monitoring.service`)
   - Fetches relevant code context from repository
   - Provides code snippets to AI for analysis
   - Enables error pattern recognition

4. **AiAnalysisController** (`com.monitoring.controller`)
   - REST endpoints for AI analysis
   - Manual trigger for incident analysis
   - Health check endpoints

## How It Works

### Incident Creation Flow

```
1. New Incident Created (via /api/incidents POST)
   ↓
2. IncidentService.createIncident() saves incident to DB
   ↓
3. analyzeAndLinkRca() is called automatically
   ↓
4. VertexAiIncidentAnalysisService.analyzeIncident() starts
   ↓
5. AI Analysis Workflow:
   a) Find Similar Historical Incidents
   b) Fetch GitHub Code Context (optional)
   c) Build Analysis Prompt
   d) Call Vertex AI Gemini API
   e) Parse Response
   ↓
6. Create IncidentRca record with AI findings
   ↓
7. Link RCA to Incident via rca_id
```

### Analysis Prompt Structure

```
You are an expert incident response engineer. Analyze the following production incident...

INCIDENT DETAILS:
- Incident Number
- Application Name
- Environment (PROD/DEV/STAGING)
- Severity
- Error Type (DB, NPE, TIMEOUT, CONNECTION, etc.)
- Description

ERROR LOG:
[Full error log or raw logs from application]

SIMILAR INCIDENTS (from database history):
- Previous incidents with same or similar errors
- Their resolutions

Please provide:
1. ROOT_CAUSE: Technical analysis (2-3 sentences)
2. RECOMMENDATION: Actionable solution (2-3 sentences)
3. CONFIDENCE: Confidence score (0-100)
```

## API Endpoints

### Create Incident with Auto-Analysis

```bash
POST /api/incidents
Content-Type: application/json

{
  "applicationName": "Payment Service",
  "environment": "PROD",
  "description": "Database connection timeout",
  "severity": "Critical",
  "errorType": "DB",
  "rawLog": "ERROR: Connection pool exhausted, max connections 50 reached...",
  "aiSummary": "DB connectivity issue",
  "createdBy": "OpsTeam"
}
```

**Response (includes auto-generated RCA):**
```json
{
  "incidentNo": "INC-1234",
  "applicationName": "Payment Service",
  "severity": "Critical",
  "rcaId": "INC-1234-RCA",
  "incidentRca": {
    "rcaId": "INC-1234-RCA",
    "incidentNo": "INC-1234",
    "rootCause": "Database connection pool exhausted due to connection leaks...",
    "recommendation": "Increase pool size from 50 to 100 and implement connection monitoring...",
    "confidenceScore": 85.5,
    "incidentRefs": "INC-1001, INC-1002"
  }
}
```

### Manual AI Analysis

```bash
POST /api/ai-analysis/analyze/{incidentNo}

Response:
{
  "rootCause": "AI-generated root cause analysis",
  "recommendation": "AI-generated recommendation",
  "confidenceScore": 82.3,
  "analysis": "Full AI response",
  "relatedIncidents": "List of similar incidents"
}
```

### AI Service Health

```bash
GET /api/ai-analysis/health

Response: "AI Analysis Service is ready. Vertex AI integration enabled."
```

## Configuration

### Application Properties

```properties
# Vertex AI Configuration
vertex-ai.project-id=hack-team-aialchemists-2026
vertex-ai.location=us-central1
vertex-ai.model=gemini-1.5-pro
vertex-ai.enabled=true
```

### GCP Setup Requirements

1. **Service Account Authentication**
   - Ensure service account has Vertex AI API access
   - Use Application Default Credentials (ADC) or service account key
   - Set `GOOGLE_APPLICATION_CREDENTIALS` environment variable:
   ```bash
   export GOOGLE_APPLICATION_CREDENTIALS=/path/to/service-account-key.json
   ```

2. **Enable Vertex AI API**
   - Go to GCP Console → APIs & Services
   - Enable "Vertex AI API"
   - Enable "Generative AI API"

3. **Set Permissions**
   - Service account needs: `aiplatform.endpoints.predict`
   - Role: `roles/aiplatform.user` or `roles/aiplatform.viewer`

## Key Entities

### IncidentDetails
- Now includes `rca_id` foreign key
- Automatic relationship to IncidentRca via @OneToOne
- RCA is populated on API response when available

### IncidentRca
- `rcaId` (Primary Key): Unique identifier
- `incidentNo` (NOT NULL): Links to incident
- `rootCause`: AI-generated root cause
- `recommendation`: AI-generated recommendation
- `incidentRefs`: Related incident references
- `confidenceScore`: Confidence in analysis (0-100)
- `createdAt`, `updatedAt`: Timestamps

### AiAnalysisResultDto
- `rootCause`: Extracted from AI response
- `recommendation`: Actionable solution
- `confidenceScore`: Numeric confidence (0-100)
- `analysis`: Full AI response
- `relatedIncidents`: Historical incident references

## AI Model Capabilities

### Gemini 1.5 Pro Features

✅ **Error Analysis**
- Parses error logs and stack traces
- Identifies error patterns
- Correlates with application context

✅ **Historical Context**
- References similar past incidents
- Suggests proven solutions
- Learns from incident history

✅ **Code Understanding**
- Analyzes relevant code snippets
- Identifies potential issues
- Suggests code-level fixes

✅ **Multi-Domain Expertise**
- Database performance issues
- Network connectivity problems
- Memory/resource leaks
- Application logic errors

## Workflow Examples

### Example 1: Database Connection Pooling

**Incident Input:**
```
Error: Connection pool exhausted, max connections 50 reached
Log shows repeated "Unable to acquire connection" messages
Application: Payment Service
```

**AI Analysis Output:**
```
ROOT_CAUSE: Database connection pool is configured with only 50 connections. 
Current load exceeds this limit, causing connection acquisition failures. 
Possible connection leaks in application code.

RECOMMENDATION: (1) Increase pool size to 100-150 connections based on peak load.
(2) Implement connection timeout monitoring. (3) Review payment service 
connection usage patterns. (4) Add connection.resetOnCheckout=true to HikariCP config.

CONFIDENCE: 88.0
```

### Example 2: Memory Leak Detection

**Incident Input:**
```
Error: OutOfMemoryError: Java heap space
Application: Order Service
Severity: Critical
```

**AI Analysis Output:**
```
ROOT_CAUSE: Persistent memory leak in Order Service detected through repeated 
OutOfMemoryError after 24 hours. Likely caused by accumulating object references 
in cache layer or event listener not properly disposed.

RECOMMENDATION: (1) Enable memory profiling with JVM flags -Xmx, -XX:+PrintGCDetails.
(2) Use YourKit/JProfiler to identify object retention paths.
(3) Check for unclosed streams/connections. (4) Review recent cache modifications.

CONFIDENCE: 75.5
```

## Debugging & Monitoring

### Check AI Service Status

```bash
curl http://localhost:8080/api/ai-analysis/health
```

### View Generated RCA via API

```bash
curl http://localhost:8080/api/incidents/INC-1234
# Shows full incidentRca object with AI analysis
```

### Enable Debug Logging

```properties
logging.level.com.monitoring.service.VertexAiIncidentAnalysisService=DEBUG
```

### Monitor Vertex AI API Usage

- GCP Console → Vertex AI → Monitoring
- Check token consumption
- Monitor API call latency

## Error Handling

- AI analysis failures don't block incident creation
- RCA is optional; incidents created successfully even if AI fails
- Failed analysis attempts are logged
- Fallback: Incidents show rcaId but incidentRca remains null

## Best Practices

1. **Provide Rich Error Context**
   - Include full stack traces in rawLog
   - Add application configuration details
   - Include timestamp and user impact info

2. **Monitor Confidence Scores**
   - Scores > 80: High confidence, act on recommendations
   - Scores 60-80: Medium confidence, verify before implementing
   - Scores < 60: Low confidence, manual review recommended

3. **Cross-Reference Historical Incidents**
   - AI returns relatedIncidents field
   - Use these to identify patterns
   - Track resolution effectiveness

4. **Iterate on Model Performance**
   - Review AI recommendations
   - Track which recommendations resolved issues
   - Provide feedback loop for model improvement

## Cost Considerations

- Vertex AI charges per token (input + output)
- Estimated cost: $0.001-0.01 per incident analysis
- Disable AI analysis if cost is concern: `vertex-ai.enabled=false`

## Future Enhancements

- [ ] Feedback loop for recommendation accuracy
- [ ] Integration with incident ticketing systems
- [ ] Automated remediation suggestions
- [ ] Multi-model comparison (Gemini vs Claude)
- [ ] Custom training on historical incidents
- [ ] Real-time log streaming for live analysis

## Troubleshooting

### Issue: "Cannot authenticate to Vertex AI"
**Solution:** Set `GOOGLE_APPLICATION_CREDENTIALS` environment variable

### Issue: "Vertex AI API not enabled"
**Solution:** Enable in GCP Console → APIs & Services

### Issue: "RCA returns null despite rcaId being set"
**Solution:** Check if AI analysis succeeded (check logs) or manually call `/api/ai-analysis/analyze/{incidentNo}`

### Issue: Analysis takes too long
**Solution:** Reduce similar incident search scope or disable GitHub code fetching

## Support & Resources

- Vertex AI Documentation: https://cloud.google.com/vertex-ai/docs
- Gemini API Guide: https://cloud.google.com/vertex-ai/docs/generative-ai/model-reference/gemini
- Application Logs: Check `/tmp/backend.log` for AI service details
