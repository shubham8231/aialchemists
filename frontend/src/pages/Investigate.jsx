import React, { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import {
  Box,
  Grid,
  Typography,
  Paper,
  TextField,
  Button,
  Stack,
  Chip,
  Divider,
  List,
  ListItem,
  ListItemIcon,
  ListItemText,
  LinearProgress,
  Dialog,
  DialogContent,
  Zoom,
} from "@mui/material";
import MainLayout from "./MainLayout";

// Direct Icon Imports for stability
import Description from "@mui/icons-material/Description";
import CloudUpload from "@mui/icons-material/CloudUpload";
import DeleteOutlinedIcon from "@mui/icons-material/DeleteOutlined";
import AutoAwesome from "@mui/icons-material/AutoAwesome";
import CheckCircle from "@mui/icons-material/CheckCircle";
import RadioButtonUnchecked from "@mui/icons-material/RadioButtonUnchecked";
import Sync from "@mui/icons-material/Sync";
import InfoOutlined from "@mui/icons-material/InfoOutlined";
import Terminal from "@mui/icons-material/Terminal";

const AI_AGENTS = [
  { id: 1, label: "Log Parsing Agent" },
  { id: 2, label: "Exception Detection Agent" },
  { id: 3, label: "Stack Trace Analyzer" },
  { id: 4, label: "Historical Incident Agent" },
  { id: 5, label: "Knowledge Base Agent" },
  { id: 6, label: "Root Cause Agent" },
  { id: 7, label: "Resolution Recommendation Agent" },
  { id: 8, label: "Incident Summary Agent" },
];

const Investigate = () => {
  const navigate = useNavigate();
  const [logs, setLogs] = useState("");
  const [isAnalyzing, setIsAnalyzing] = useState(false);
  const [activeStep, setActiveStep] = useState(0);
  const [createdIncidentId, setCreatedIncidentId] = useState(null);

  // Simulation logic for AI workflow
  useEffect(() => {
    let timer;
    if (isAnalyzing && activeStep < AI_AGENTS.length) {
      timer = setTimeout(() => {
        setActiveStep((prev) => prev + 1);
      }, 800); // Progress every 800ms
    } else if (activeStep === AI_AGENTS.length) {
      // Once finished, redirect to the created incident
      timer = setTimeout(() => {
        setIsAnalyzing(false);
        if (createdIncidentId) {
          navigate(`/incident/${createdIncidentId}`);
        }
      }, 1000);
    }
    return () => clearTimeout(timer);
  }, [isAnalyzing, activeStep, navigate, createdIncidentId]);

  const handleStartInvestigation = async () => {
    if (!logs.trim()) return alert("Please paste logs or upload a file first.");

    try {
      // Create incident with the provided logs
      const response = await fetch("http://localhost:8080/api/incidents", {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
        },
        body: JSON.stringify({
          applicationName: "AI Investigation",
          environment: "PROD",
          description: logs.substring(0, 200), // First 200 chars as description
          severity: "High",
          errorType: "GENERAL",
          rawLog: logs,
          aiSummary: "Analysis pending",
          createdBy: "Investigation Tool",
        }),
      });

      if (!response.ok) {
        throw new Error("Failed to create incident");
      }

      const incidentData = await response.json();
      setCreatedIncidentId(incidentData.incidentNo);
      setIsAnalyzing(true);
      setActiveStep(0);
    } catch (error) {
      console.error("Error creating incident:", error);
      alert("Failed to create incident. Please try again.");
    }
  };

  const handleClear = () => setLogs("");

  return (
    <MainLayout>
      <Box sx={{ p: { xs: 2, md: 4 }, maxWidth: "1400px", margin: "0 auto" }}>
        {/* Header Area */}
        <Box sx={{ mb: 4, display: "flex", alignItems: "center", gap: 2 }}>
          <Box
            sx={{
              p: 1,
              borderRadius: 2,
              bgcolor: "primary.main",
              color: "white",
            }}
          >
            <Terminal />
          </Box>
          <Box>
            <Typography variant="h4" sx={{ fontWeight: 800 }}>
              AI Investigator
            </Typography>
            <Typography variant="body2" color="textSecondary">
              Initiate a deep-scan investigation using autonomous agents.
            </Typography>
          </Box>
        </Box>

        <Grid container spacing={4}>
          {/* LEFT COLUMN: EDITOR */}
          <Grid item xs={12} md={7}>
            <Paper
              elevation={0}
              sx={{ p: 3, borderRadius: 3, border: "1px solid #e0e0e0" }}
            >
              <Typography
                variant="subtitle1"
                sx={{
                  fontWeight: 700,
                  mb: 2,
                  display: "flex",
                  alignItems: "center",
                }}
              >
                <Description fontSize="small" sx={{ mr: 1 }} /> Paste System
                Logs
              </Typography>

              <TextField
                multiline
                rows={10}
                fullWidth
                placeholder="Paste raw log data, stack traces, or console output here..."
                value={logs}
                onChange={(e) => setLogs(e.target.value)}
                variant="outlined"
                sx={{
                  "& .MuiOutlinedInput-root": {
                    fontFamily: "'Fira Code', 'Courier New', monospace",
                    fontSize: "0.85rem",
                    bgcolor: "#fafafa",
                    "& fieldset": { borderColor: "#e0e0e0" },
                  },
                }}
              />
              <Box sx={{ mt: 3 }}>
                <Button
                  component="label"
                  variant="outlined"
                  startIcon={<CloudUpload />}
                  sx={{ borderRadius: 2, textTransform: "none" }}
                >
                  Upload Log File
                  <input type="file" hidden />
                </Button>
              </Box>

              <Box
                sx={{
                  mt: 3,
                  display: "flex",
                  justifyContent: "space-between",
                  alignItems: "center",
                }}
              >
                <Stack direction="row" spacing={2}>
                  <Button
                    onClick={handleClear}
                    startIcon={<DeleteOutlinedIcon />}
                    color="inherit"
                    sx={{ textTransform: "none" }}
                  >
                    Clear
                  </Button>
                  <Button
                    onClick={handleStartInvestigation}
                    variant="contained"
                    startIcon={<AutoAwesome />}
                    sx={{
                      borderRadius: 2,
                      px: 4,
                      textTransform: "none",
                      fontWeight: "bold",
                    }}
                  >
                    Start AI Investigation
                  </Button>
                </Stack>
              </Box>
            </Paper>
          </Grid>

          {/* RIGHT COLUMN: TIPS & TECH */}
          <Grid item xs={12} md={5}>
            <Stack spacing={3}>
              <Paper
                elevation={0}
                sx={{
                  p: 3,
                  borderRadius: 3,
                  bgcolor: "#f0f4f8",
                  border: "1px solid #d1d9e6",
                }}
              >
                <Typography
                  variant="subtitle2"
                  sx={{
                    fontWeight: 800,
                    mb: 2,
                    display: "flex",
                    alignItems: "center",
                  }}
                >
                  <InfoOutlined fontSize="small" sx={{ mr: 1 }} /> INVESTIGATION
                  TIPS
                </Typography>
                <List dense disablePadding>
                  {[
                    "Paste complete logs for context",
                    "Minimum 50 lines recommended",
                    "Include multi-line stack traces",
                    "Include Correlation IDs if available",
                  ].map((text, i) => (
                    <ListItem key={i} disableGutters>
                      <ListItemIcon
                        sx={{ minWidth: 28, color: "success.main" }}
                      >
                        <CheckCircle sx={{ fontSize: 16 }} />
                      </ListItemIcon>
                      <ListItemText
                        primary={text}
                        primaryTypographyProps={{ variant: "body2" }}
                      />
                    </ListItem>
                  ))}
                </List>
              </Paper>
            </Stack>
          </Grid>
        </Grid>

        {/* --- AI WORKFLOW OVERLAY --- */}
        <Dialog
          open={isAnalyzing}
          fullWidth
          maxWidth="sm"
          TransitionComponent={Zoom}
          PaperProps={{
            sx: { borderRadius: 4, p: 2 },
          }}
        >
          <DialogContent>
            <Box sx={{ textAlign: "center", mb: 4 }}>
              <AutoAwesome
                sx={{
                  fontSize: 40,
                  color: "#7b1fa2",
                  mb: 1,
                  animation: "pulse 2s infinite",
                }}
              />
              <Typography variant="h5" sx={{ fontWeight: 800 }}>
                AI Investigation Running...
              </Typography>
              <Typography variant="body2" color="textSecondary">
                The swarm of agents is analyzing your data.
              </Typography>
            </Box>

            <List>
              {AI_AGENTS.map((agent, index) => {
                const isCompleted = index < activeStep;
                const isActive = index === activeStep;

                return (
                  <ListItem
                    key={agent.id}
                    sx={{ opacity: isCompleted || isActive ? 1 : 0.4 }}
                  >
                    <ListItemIcon sx={{ minWidth: 40 }}>
                      {isCompleted ? (
                        <CheckCircle sx={{ color: "success.main" }} />
                      ) : isActive ? (
                        <Sync
                          sx={{
                            color: "#7b1fa2",
                            animation: "spin 2s linear infinite",
                          }}
                        />
                      ) : (
                        <RadioButtonUnchecked sx={{ color: "grey.400" }} />
                      )}
                    </ListItemIcon>
                    <ListItemText
                      primary={agent.label}
                      primaryTypographyProps={{
                        fontWeight: isActive ? 700 : 400,
                        color: isActive ? "#7b1fa2" : "inherit",
                      }}
                    />
                  </ListItem>
                );
              })}
            </List>

            <Box sx={{ mt: 4 }}>
              <LinearProgress
                variant="determinate"
                value={(activeStep / AI_AGENTS.length) * 100}
                sx={{
                  height: 8,
                  borderRadius: 4,
                  bgcolor: "#f3e5f5",
                  "& .MuiLinearProgress-bar": { bgcolor: "#7b1fa2" },
                }}
              />
            </Box>
          </DialogContent>
        </Dialog>

        <style>
          {`
          @keyframes spin {
            from { transform: rotate(0deg); }
            to { transform: rotate(360deg); }
          }
          @keyframes pulse {
            0% { transform: scale(1); opacity: 1; }
            50% { transform: scale(1.1); opacity: 0.7; }
            100% { transform: scale(1); opacity: 1; }
          }
        `}
        </style>
      </Box>
    </MainLayout>
  );
};

export default Investigate;
