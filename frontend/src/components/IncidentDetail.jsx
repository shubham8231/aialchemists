import React, { useState, useEffect } from 'react';
import { useParams } from 'react-router-dom';
import {
  Box,
  Container,
  Typography,
  Paper,
  Chip,
  Grid,
  Divider,
  LinearProgress,
  List,
  ListItem,
  ListItemIcon,
  ListItemText,
  Button,
  Checkbox,
  Stack,
  Avatar,
  Card,
  CardContent,
  Tooltip,
  CircularProgress,
} from '@mui/material';
import {
  Timeline,
  TimelineItem,
  TimelineSeparator,
  TimelineConnector,
  TimelineContent,
  TimelineDot,
  TimelineOppositeContent,
} from '@mui/lab';
import {
  CheckCircle as CheckCircleIcon,
  Schedule as ScheduleIcon,
  Troubleshoot as ErrorIcon,
  AutoAwesome as AIIcon,
  Description as LogIcon,
  History as HistoryIcon,
  Search as SearchIcon,
  Build as BuildIcon,
  Sync as SyncIcon,
} from '@mui/icons-material';

import MainLayout from '../pages/MainLayout';

const IncidentDetail = () => {
  const { id } = useParams();
  const [incident, setIncident] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [checkedActions, setCheckedActions] = useState([]);
  const [auditLogs, setAuditLogs] = useState([]);
  const [rcaDetails, setRcaDetails] = useState(null);

  useEffect(() => {
    fetchIncidentDetails();

    // Poll for RCA updates every 5 seconds
    const interval = setInterval(fetchIncidentDetails, 5000);
    return () => clearInterval(interval);
  }, [id]);

  const fetchIncidentDetails = async () => {
    try {
      setLoading(true);
      const response = await fetch(`http://localhost:8080/api/incidents/${id}`);
      if (!response.ok) throw new Error("Failed to fetch incident details");
      const data = await response.json();

      // Fetch audit logs
      try {
        const auditResponse = await fetch(`http://localhost:8080/api/audit-logs/incident/${id}/rca`);
        if (auditResponse.ok) {
          const auditData = await auditResponse.json();
          setAuditLogs(auditData);
        }
      } catch (auditErr) {
        console.log("Audit logs not available:", auditErr);
      }

      // Transform backend data to match component expectations
      const aiProgress = data.incidentRca ? 100 : (auditLogs.length > 0 ? 75 : 0);
      const rcaStatus = auditLogs.length > 0 ? auditLogs[0].status : "PENDING";

      const transformed = {
        id: data.incidentNo,
        title: data.description || data.errorType,
        priority: data.severity,
        status: data.status,
        aiProgress: aiProgress,
        environment: data.environment,
        application: data.applicationName,
        errorType: data.errorType,
        rawLog: data.rawLog,
        aiSummary: data.aiSummary,
        createdBy: data.createdBy,
        createdAt: data.createdAt,
        updatedAt: data.updatedAt,
        timeline: [
          { time: new Date(data.createdAt).toLocaleTimeString(), label: "Error detected", status: "completed" },
          { time: new Date(data.createdAt).toLocaleTimeString(), label: "Incident created", status: "completed" },
          { time: new Date(data.updatedAt).toLocaleTimeString(), label: "Updated", status: "completed" },
        ],
        investigationSteps: [
          { label: "Log Analysis", status: "success" },
          { label: "Error Extraction", status: "success" },
          { label: "RCA Synthesis", status: rcaStatus === "COMPLETED" ? "success" : rcaStatus === "FAILED" ? "error" : "loading" },
        ],
        rca: {
          summary: data.incidentRca?.rootCause || data.aiSummary || "Analysis in progress",
          confidence: data.incidentRca?.confidenceScore || 0,
        },
        evidence: [
          { type: "Raw Log", desc: data.rawLog?.substring(0, 100) || "No logs" },
          { type: "Error Type", desc: data.errorType },
          { type: "Environment", desc: data.environment },
        ],
        actions: [
          `Review ${data.errorType} error`,
          "Check system logs",
          "Verify service status",
        ],
      };

      // Set RCA details from the incident
      if (data.incidentRca) {
        setRcaDetails(data.incidentRca);
      }

      setIncident(transformed);
      setError(null);
    } catch (err) {
      setError(err.message);
      console.error("Error fetching incident:", err);
    } finally {
      setLoading(false);
    }
  };

  const formatDate = (dateString) => {
    if (!dateString) return 'N/A';
    try {
      const date = new Date(dateString);
      if (isNaN(date.getTime())) return 'N/A';
      return date.toLocaleString('en-US', {
        year: 'numeric',
        month: 'short',
        day: 'numeric',
        hour: '2-digit',
        minute: '2-digit',
        second: '2-digit'
      });
    } catch (e) {
      return 'N/A';
    }
  };

  const handleToggle = (value) => () => {
    const currentIndex = checkedActions.indexOf(value);
    const newChecked = [...checkedActions];
    if (currentIndex === -1) newChecked.push(value);
    else newChecked.splice(currentIndex, 1);
    setCheckedActions(newChecked);
  };

  if (loading) {
    return (
      <MainLayout>
        <Box sx={{ display: "flex", justifyContent: "center", alignItems: "center", height: "400px" }}>
          <CircularProgress />
        </Box>
      </MainLayout>
    );
  }

  if (error || !incident) {
    return (
      <MainLayout>
        <Box sx={{ p: 4 }}>
          <Typography color="error">Error: {error || "Incident not found"}</Typography>
        </Box>
      </MainLayout>
    );
  }

    return (
      <MainLayout>
    <Box sx={{ bgcolor: '#f4f7f9', minHeight: '100vh', py: 4 }}>
      <Container maxWidth="lg">
        
        {/* --- HEADER SECTION --- */}
        <Paper elevation={0} sx={{ p: 3, mb: 3, borderRadius: 2, borderLeft: '6px solid #d32f2f' }}>
          <Grid container justifyContent="space-between" alignItems="flex-start" spacing={2}>
            <Grid item xs={12} md={8}>
              <Typography variant="overline" color="textSecondary" sx={{ fontWeight: 'bold' }}>
                {incident.id}
              </Typography>
              <Typography variant="h4" sx={{ fontWeight: 800, mb: 1 }}>
                {incident.title}
              </Typography>
              <Stack direction="row" spacing={2} alignItems="center" sx={{ mb: 2 }}>
                <Chip label={incident.priority} color="error" size="small" sx={{ fontWeight: 'bold' }} />
                <Chip label={incident.status} variant="outlined" size="small" sx={{ fontWeight: 'bold' }} />
                <Divider orientation="vertical" flexItem />
                <Box sx={{ display: 'flex', alignItems: 'center', flexGrow: 1 }}>
                  <AIIcon sx={{ color: '#7b1fa2', mr: 1, fontSize: 20 }} />
                  <Typography variant="body2" sx={{ mr: 2, fontWeight: 600, color: '#7b1fa2' }}>
                    AI Analysis {incident.aiProgress}%
                  </Typography>
                  <LinearProgress
                    variant="determinate"
                    value={incident.aiProgress}
                    sx={{ flexGrow: 1, height: 8, borderRadius: 5, bgcolor: '#e1bee7', '& .MuiLinearProgress-bar': { bgcolor: '#7b1fa2' } }}
                  />
                </Box>
              </Stack>

              {/* Created By & Time Metadata */}
              <Stack direction="row" spacing={3} sx={{ mt: 2, pt: 2, borderTop: '1px solid #eee' }}>
                <Box>
                  <Typography variant="caption" color="textSecondary" sx={{ fontWeight: 600 }}>
                    Created By
                  </Typography>
                  <Typography variant="body2" sx={{ fontWeight: 500 }}>
                    {incident.createdBy || 'N/A'}
                  </Typography>
                </Box>
                <Box>
                  <Typography variant="caption" color="textSecondary" sx={{ fontWeight: 600 }}>
                    Created At
                  </Typography>
                  <Typography variant="body2" sx={{ fontWeight: 500 }}>
                    {incident.createdAt ? formatDate(incident.createdAt) : 'N/A'}
                  </Typography>
                </Box>
                <Box>
                  <Typography variant="caption" color="textSecondary" sx={{ fontWeight: 600 }}>
                    Last Updated
                  </Typography>
                  <Typography variant="body2" sx={{ fontWeight: 500 }}>
                    {incident.updatedAt ? formatDate(incident.updatedAt) : 'N/A'}
                  </Typography>
                </Box>
              </Stack>
            </Grid>
          </Grid>
        </Paper>

        <Grid container spacing={2} sx={{ alignItems: 'flex-start' }}>
          {/* --- LEFT COLUMN: AI & RCA --- */}
          <Grid item xs={12} md={8}>
            
            {/* AI Investigation Section */}
            <Card sx={{ mb: 3, borderRadius: 2, boxShadow: '0 4px 20px rgba(0,0,0,0.05)' }}>
              <CardContent>
                <Typography variant="h6" gutterBottom sx={{ display: 'flex', alignItems: 'center' }}>
                  <AIIcon sx={{ mr: 1, color: '#7b1fa2' }} /> AI INVESTIGATION
                </Typography>
                <List>
                  {incident.investigationSteps.map((step, index) => (
                    <ListItem key={index} sx={{ py: 0.5 }}>
                      <ListItemIcon sx={{ minWidth: 35 }}>
                        {step.status === 'success' ? (
                          <CheckCircleIcon sx={{ color: '#2e7d32' }} />
                        ) : (
                          <SyncIcon sx={{ color: '#7b1fa2', animation: 'spin 2s linear infinite' }} />
                        )}
                      </ListItemIcon>
                      <ListItemText 
                        primary={step.label} 
                        primaryTypographyProps={{ 
                          variant: 'body1', 
                          fontWeight: step.status === 'loading' ? 'bold' : 'normal',
                          color: step.status === 'loading' ? '#7b1fa2' : 'textPrimary'
                        }} 
                      />
                    </ListItem>
                  ))}
                </List>
              </CardContent>
            </Card>

            {/* Root Cause Analysis Section */}
            <Card sx={{ mb: 3, borderRadius: 2, bgcolor: '#f3e5f5', border: '1px solid #ce93d8' }}>
              <CardContent>
                <Typography variant="h6" gutterBottom color="#4a148c">
                  ROOT CAUSE ANALYSIS
                </Typography>

                {rcaDetails ? (
                  <>
                    <Box sx={{ mb: 3 }}>
                      <Typography variant="subtitle2" sx={{ fontWeight: 'bold', mb: 1, color: '#4a148c' }}>
                        Root Cause:
                      </Typography>
                      <Typography variant="body2" sx={{ mb: 2, p: 2, bgcolor: 'white', borderRadius: 1 }}>
                        {rcaDetails.rootCause || 'Not available'}
                      </Typography>
                    </Box>

                    <Box sx={{ mb: 3 }}>
                      <Typography variant="subtitle2" sx={{ fontWeight: 'bold', mb: 1, color: '#4a148c' }}>
                        Recommendation:
                      </Typography>
                      <Typography variant="body2" sx={{ mb: 2, p: 2, bgcolor: 'white', borderRadius: 1 }}>
                        {rcaDetails.recommendation || 'No recommendation available'}
                      </Typography>
                    </Box>

                    {rcaDetails.incidentRefs && (
                      <Box sx={{ mb: 2 }}>
                        <Typography variant="subtitle2" sx={{ fontWeight: 'bold', mb: 1, color: '#4a148c' }}>
                          Related Incidents:
                        </Typography>
                        <Typography variant="body2" sx={{ p: 2, bgcolor: 'white', borderRadius: 1, fontFamily: 'monospace' }}>
                          {rcaDetails.incidentRefs}
                        </Typography>
                      </Box>
                    )}

                    <Box sx={{ display: 'flex', alignItems: 'center', mt: 2 }}>
                      <Typography variant="body2" sx={{ mr: 1, fontWeight: 'bold' }}>
                        Confidence:
                      </Typography>
                      <Chip
                        label={`${rcaDetails.confidenceScore?.toFixed(1) || 0}%`}
                        size="small"
                        sx={{ bgcolor: '#4a148c', color: 'white', fontWeight: 'bold' }}
                      />
                    </Box>
                  </>
                ) : (
                  <Typography variant="body2" color="textSecondary">
                    {incident.rca.summary}
                  </Typography>
                )}
              </CardContent>
            </Card>

            {/* Audit Log Section */}
            {auditLogs.length > 0 && (
              <Card sx={{ mb: 3, borderRadius: 2, bgcolor: '#e8f5e9', border: '1px solid #81c784' }}>
                <CardContent>
                  <Typography variant="h6" gutterBottom color="#2e7d32">
                    🔍 RCA Analysis Progress
                  </Typography>
                  <Timeline sx={{ p: 0, m: 0 }}>
                    {auditLogs.map((log, index) => (
                      <TimelineItem key={index} sx={{ mb: 1 }}>
                        <TimelineOppositeContent sx={{ flex: 0.2, fontSize: '0.75rem', fontWeight: 600 }}>
                          {new Date(log.createdAt).toLocaleTimeString()}
                        </TimelineOppositeContent>
                        <TimelineSeparator>
                          <TimelineDot sx={{
                            bgcolor: log.status === 'COMPLETED' ? '#2e7d32' :
                                   log.status === 'FAILED' ? '#d32f2f' : '#4a148c',
                            width: 24,
                            height: 24
                          }}>
                            {log.status === 'COMPLETED' ? <CheckCircleIcon sx={{ fontSize: 16, color: 'white' }} /> :
                             log.status === 'FAILED' ? <ErrorIcon sx={{ fontSize: 16, color: 'white' }} /> :
                             <SyncIcon sx={{ fontSize: 16, color: 'white' }} />}
                          </TimelineDot>
                          {index !== auditLogs.length - 1 && <TimelineConnector />}
                        </TimelineSeparator>
                        <TimelineContent sx={{ py: '2px', px: 2 }}>
                          <Typography variant="body2" sx={{ fontWeight: 600 }}>
                            {log.message}
                          </Typography>
                          {log.details && (
                            <Typography variant="caption" color="textSecondary" sx={{ display: 'block', mt: 0.5 }}>
                              {log.details}
                            </Typography>
                          )}
                          {log.durationMs && (
                            <Typography variant="caption" color="textSecondary" sx={{ display: 'block' }}>
                              ⏱️ {log.durationMs}ms
                            </Typography>
                          )}
                        </TimelineContent>
                      </TimelineItem>
                    ))}
                  </Timeline>
                </CardContent>
              </Card>
            )}

            {/* Evidence Section */}
            <Typography variant="h6" gutterBottom sx={{ mt: 4 }}>EVIDENCE</Typography>
            <Grid container spacing={2}>
              {incident.evidence.map((item, idx) => (
                <Grid item xs={12} sm={4} key={idx}>
                  <Paper variant="outlined" sx={{ p: 2, textAlign: 'center', height: '100%', borderRadius: 2 }}>
                    <LogIcon color="action" sx={{ mb: 1 }} />
                    <Typography variant="subtitle2" display="block" sx={{ fontWeight: 'bold' }}>{item.type}</Typography>
                    <Typography variant="caption" color="textSecondary">{item.desc}</Typography>
                  </Paper>
                </Grid>
              ))}
            </Grid>

            {/* Recommended Actions */}
            <Box sx={{ mt: 4, mb: 10 }}>
              <Typography variant="h6" gutterBottom>RECOMMENDED ACTIONS</Typography>
              <Paper sx={{ borderRadius: 2 }}>
                <List>
                  {incident.actions.map((action, index) => (
                    <ListItem key={index} divider={index !== incident.actions.length - 1}>
                      <Checkbox
                        edge="start"
                        checked={checkedActions.indexOf(action) !== -1}
                        tabIndex={-1}
                        disableRipple
                        onChange={handleToggle(action)}
                      />
                      <ListItemText primary={action} />
                    </ListItem>
                  ))}
                </List>
              </Paper>
            </Box>
          </Grid>

          {/* --- RIGHT COLUMN: TIMELINE --- */}
          <Grid item xs={12} md={4} sx={{ display: 'flex', flexDirection: 'column' }}>
            <Paper sx={{
              p: 3,
              borderRadius: 3,
              minHeight: '600px',
              background: 'linear-gradient(135deg, #f5f7fa 0%, #c3cfe2 100%)',
              position: { md: 'sticky' },
              top: { md: '20px' },
              height: 'fit-content'
            }}>
              <Box sx={{ display: 'flex', alignItems: 'center', mb: 3, pb: 2, borderBottom: '2px solid #667eea' }}>
                <HistoryIcon sx={{ mr: 1.5, color: '#667eea', fontSize: 28 }} />
                <Typography variant="h6" sx={{ fontWeight: 700, color: '#1a1a1a' }}>
                  INCIDENT TIMELINE
                </Typography>
              </Box>

              <Timeline sx={{ p: 0, m: 0, '& .MuiTimelineItem-root': { minHeight: '80px' } }}>
                {incident.timeline.map((item, index) => (
                  <TimelineItem key={index} sx={{ mb: 2 }}>
                    <TimelineOppositeContent sx={{ flex: 0.25, fontSize: '0.8rem', fontWeight: 600, color: '#667eea', px: 1 }}>
                      <Box sx={{ bgcolor: 'rgba(102, 126, 234, 0.1)', p: 1, borderRadius: 1 }}>
                        {item.time}
                      </Box>
                    </TimelineOppositeContent>
                    <TimelineSeparator>
                      <TimelineDot
                        sx={{
                          boxShadow: item.status === 'completed' ? '0 0 0 4px #e8eef7' : 'none',
                          bgcolor: item.status === 'completed' ? '#667eea' : '#cbd5e0',
                          width: 32,
                          height: 32
                        }}
                      >
                        {item.status === 'completed' ? (
                          <CheckCircleIcon sx={{ fontSize: 20, color: 'white' }} />
                        ) : (
                          <ScheduleIcon sx={{ fontSize: 20, color: 'white' }} />
                        )}
                      </TimelineDot>
                      {index !== incident.timeline.length - 1 && (
                        <TimelineConnector sx={{ bgcolor: item.status === 'completed' ? '#667eea' : '#cbd5e0', height: 30 }} />
                      )}
                    </TimelineSeparator>
                    <TimelineContent sx={{ py: '6px', px: 2, flex: 1 }}>
                      <Paper
                        sx={{
                          p: 2,
                          bgcolor: item.status === 'completed' ? 'white' : '#f8f9fa',
                          borderLeft: `4px solid ${item.status === 'completed' ? '#667eea' : '#cbd5e0'}`,
                          borderRadius: '0 8px 8px 0',
                          boxShadow: item.status === 'completed' ? '0 2px 8px rgba(0,0,0,0.08)' : 'none'
                        }}
                      >
                        <Typography
                          variant="body2"
                          sx={{
                            fontWeight: 600,
                            color: item.status === 'completed' ? '#1a1a1a' : '#666',
                            mb: 0.5
                          }}
                        >
                          {item.label}
                        </Typography>
                        <Typography
                          variant="caption"
                          sx={{
                            color: item.status === 'completed' ? '#667eea' : '#999',
                            display: 'flex',
                            alignItems: 'center',
                            mt: 0.5
                          }}
                        >
                          <Box
                            component="span"
                            sx={{
                              width: 8,
                              height: 8,
                              borderRadius: '50%',
                              bgcolor: item.status === 'completed' ? '#2e7d32' : '#f9a825',
                              mr: 0.5,
                              display: 'inline-block'
                            }}
                          />
                          {item.status === 'completed' ? 'Completed' : 'Pending'}
                        </Typography>
                      </Paper>
                    </TimelineContent>
                  </TimelineItem>
                ))}
              </Timeline>

              {/* Timeline Summary Footer */}
              <Box sx={{ mt: 3, pt: 2, borderTop: '2px solid rgba(102, 126, 234, 0.2)', bgcolor: 'rgba(102, 126, 234, 0.05)', p: 2, borderRadius: 1 }}>
                <Typography variant="caption" sx={{ fontWeight: 600, color: '#667eea' }}>
                  📊 Status Summary
                </Typography>
                <Box sx={{ display: 'flex', justifyContent: 'space-around', mt: 1.5 }}>
                  <Box sx={{ textAlign: 'center' }}>
                    <Typography variant="h6" sx={{ fontWeight: 800, color: '#2e7d32' }}>
                      {incident.timeline.filter(t => t.status === 'completed').length}
                    </Typography>
                    <Typography variant="caption" color="textSecondary">Completed</Typography>
                  </Box>
                  <Box sx={{ textAlign: 'center' }}>
                    <Typography variant="h6" sx={{ fontWeight: 800, color: '#f9a825' }}>
                      {incident.timeline.filter(t => t.status !== 'completed').length}
                    </Typography>
                    <Typography variant="caption" color="textSecondary">Pending</Typography>
                  </Box>
                  <Box sx={{ textAlign: 'center' }}>
                    <Typography variant="h6" sx={{ fontWeight: 800, color: '#667eea' }}>
                      {incident.timeline.length}
                    </Typography>
                    <Typography variant="caption" color="textSecondary">Total Events</Typography>
                  </Box>
                </Box>
              </Box>
            </Paper>
          </Grid>
        </Grid>

        {/* --- STICKY ACTION FOOTER --- */}
        <Paper 
          elevation={10} 
          sx={{ 
            position: 'fixed', 
            bottom: 0, 
            left: 0, 
            right: 0, 
            p: 2, 
            bgcolor: 'white', 
            zIndex: 1000,
            borderTop: '1px solid #eee'
          }}
        >
          <Container maxWidth="md">
            <Stack direction="row" spacing={2} justifyContent="flex-end">
              <Button variant="outlined" color="inherit">Snooze</Button>
              <Button variant="outlined" color="primary">Acknowledge</Button>
              <Button variant="contained" color="success" sx={{ px: 4 }}>Mark Resolved</Button>
            </Stack>
          </Container>
        </Paper>

      </Container>

      {/* Animation Styles */}
      <style>
        {`
          @keyframes spin {
            from { transform: rotate(0deg); }
            to { transform: rotate(360deg); }
          }
        `}
      </style>
            </Box>
            </MainLayout>
  );
};

export default IncidentDetail;