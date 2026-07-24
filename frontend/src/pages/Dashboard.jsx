import React, { useState, useEffect } from "react";
import {
  Box,
  Grid,
  Typography,
  Paper,
  Chip,
  Table,
  TableBody,
  TableCell,
  TableContainer,
  TableHead,
  TableRow,
  LinearProgress,
  Button,
  Stack,
  Card,
  CardContent,
  IconButton,
  CircularProgress,
} from "@mui/material";
import {
  TrendingUp,
  AutoAwesome,
  Security,
  ArrowForwardIos,
  NotificationsActive,
} from "@mui/icons-material";
import ErrorOutlinedIcon from "@mui/icons-material/ErrorOutlined";
import CheckCircleOutlineOutlinedIcon from "@mui/icons-material/CheckCircleOutlineOutlined";
import MainLayout from "./MainLayout";

const Dashboard = ({ onNavigate }) => {
  const [incidents, setIncidents] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    fetchIncidents();
  }, []);

  const fetchIncidents = async () => {
    try {
      setLoading(true);
      const response = await fetch("http://localhost:8080/api/incidents");
      if (!response.ok) throw new Error("Failed to fetch incidents");
      const data = await response.json();
      setIncidents(data.content || []);
      setError(null);
    } catch (err) {
      setError(err.message);
      console.error("Error fetching incidents:", err);
    } finally {
      setLoading(false);
    }
  };

  const calculateMetrics = () => {
    const active = incidents.filter((i) => i.status === "Investigating").length;
    const critical = incidents.filter((i) => i.severity === "Critical").length;
    const resolved = incidents.filter((i) => i.status === "Resolved").length;

    return [
      {
        label: "Active Incidents",
        value: active.toString(),
        icon: <NotificationsActive />,
        color: "#2196f3",
        trend: `${active} investigating`,
      },
      {
        label: "Critical",
        value: critical.toString(),
        icon: <ErrorOutlinedIcon />,
        color: "#d32f2f",
        trend: "Requires attention",
      },
      {
        label: "Resolved",
        value: resolved.toString(),
        icon: <CheckCircleOutlineOutlinedIcon />,
        color: "#2e7d32",
        trend: `${resolved} total resolved`,
      },
    ];
  };

  const getDisplayIncidents = () => {
    return incidents.slice(0, 10).map((inc) => ({
      id: inc.incidentNo,
      title: inc.description || inc.errorType,
      priority: inc.severity.toUpperCase(),
      status: inc.status,
      application: inc.applicationName,
      errorType: inc.errorType,
    }));
  };

  const getPriorityColor = (priority) => {
    if (priority === "CRITICAL" || priority === "HIGH") return "error";
    if (priority === "MEDIUM") return "warning";
    return "success";
  };

  const handleIncidentClick = (id) => {
    console.log(`Navigating to ${id}`);
    if (onNavigate) onNavigate(id);
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

  const METRICS = calculateMetrics();
  const displayIncidents = getDisplayIncidents();

  return (
    <MainLayout>
      <Box sx={{ p: { xs: 2, md: 4 }, maxWidth: "1400px", margin: "0 auto" }}>
        {error && (
          <Box sx={{ mb: 2, p: 2, bgcolor: "#ffebee", borderRadius: 1 }}>
            <Typography color="error">⚠️ Error: {error}</Typography>
          </Box>
        )}

        {/* HEADER SECTION */}
        <Box sx={{ mb: 4 }}>
          <Typography
            variant="h4"
            sx={{ fontWeight: 800, color: "text.primary" }}
          >
            Incident Intelligence
          </Typography>
          <Typography variant="body2" color="textSecondary">
            Real-time AI-assisted monitoring and system health overview.
          </Typography>
        </Box>

        {/* METRIC CARDS */}
        <Grid container spacing={3} sx={{ mb: 4 }}>
          {METRICS.map((metric, index) => (
            <Grid item xs={12} sm={4} key={index}>
              <Paper
                elevation={0}
                sx={{
                  p: 3,
                  borderRadius: 3,
                  border: "1px solid #e0e0e0",
                  display: "flex",
                  alignItems: "center",
                  justifyContent: "space-between",
                  transition: "transform 0.2s",
                  "&:hover": {
                    transform: "translateY(-4px)",
                    boxShadow: "0 4px 20px rgba(0,0,0,0.05)",
                  },
                }}
              >
                <Box>
                  <Typography
                    variant="overline"
                    sx={{ fontWeight: "bold", color: "text.secondary" }}
                  >
                    {metric.label}
                  </Typography>
                  <Typography variant="h3" sx={{ fontWeight: 800, my: 0.5 }}>
                    {metric.value}
                  </Typography>
                  <Typography
                    variant="caption"
                    sx={{
                      color: metric.color,
                      fontWeight: 600,
                      display: "flex",
                      alignItems: "center",
                    }}
                  >
                    <TrendingUp sx={{ fontSize: 14, mr: 0.5 }} /> {metric.trend}
                  </Typography>
                </Box>
                <Box
                  sx={{
                    p: 2,
                    borderRadius: "50%",
                    bgcolor: `${metric.color}15`,
                    color: metric.color,
                  }}
                >
                  {metric.icon}
                </Box>
              </Paper>
            </Grid>
          ))}
        </Grid>

        <Grid container spacing={3}>
          {/* LEFT COLUMN: ACTIVE INCIDENTS */}
          <Grid item xs={12} md={8}>
            <Paper
              elevation={0}
              sx={{ p: 3, borderRadius: 3, border: "1px solid #e0e0e0", mb: 3 }}
            >
              <Box
                sx={{
                  display: "flex",
                  justifyContent: "space-between",
                  alignItems: "center",
                  mb: 3,
                }}
              >
                <Typography variant="h6" sx={{ fontWeight: 700 }}>
                  Active Incidents
                </Typography>
                <Button size="small">View All</Button>
              </Box>
              <TableContainer>
                <Table>
                  <TableHead sx={{ bgcolor: "#f8f9fa" }}>
                    <TableRow>
                      <TableCell sx={{ fontWeight: "bold" }}>ID</TableCell>
                      <TableCell sx={{ fontWeight: "bold" }}>
                        Incident Name
                      </TableCell>
                      <TableCell sx={{ fontWeight: "bold" }}>
                        Priority
                      </TableCell>
                      <TableCell sx={{ fontWeight: "bold" }}>Status</TableCell>
                      <TableCell align="right"></TableCell>
                    </TableRow>
                  </TableHead>
                  <TableBody>
                    {displayIncidents.length > 0 ? (
                      displayIncidents.map((row) => (
                        <TableRow
                          key={row.id}
                          hover
                          onClick={() => handleIncidentClick(row.id)}
                          sx={{
                            cursor: "pointer",
                            "&:last-child td, &:last-child th": { border: 0 },
                          }}
                        >
                          <TableCell
                            sx={{ fontWeight: 600, color: "primary.main" }}
                          >
                            {row.id}
                          </TableCell>
                          <TableCell sx={{ fontWeight: 500 }}>
                            <Box>
                              <Typography variant="body2" sx={{ fontWeight: 600 }}>
                                {row.title}
                              </Typography>
                              <Typography variant="caption" color="textSecondary">
                                {row.application}
                              </Typography>
                            </Box>
                          </TableCell>
                          <TableCell>
                            <Chip
                              label={row.priority}
                              size="small"
                              color={getPriorityColor(row.priority)}
                              sx={{ fontWeight: "bold", borderRadius: "4px" }}
                            />
                          </TableCell>
                          <TableCell>
                            <Typography
                              variant="caption"
                              sx={{
                                fontWeight: "bold",
                                color: row.status === "Resolved" ? "#2e7d32" : "#d32f2f",
                              }}
                            >
                              {row.status}
                            </Typography>
                          </TableCell>
                          <TableCell align="right">
                            <IconButton size="small">
                              <ArrowForwardIos sx={{ fontSize: 14 }} />
                            </IconButton>
                          </TableCell>
                        </TableRow>
                      ))
                    ) : (
                      <TableRow>
                        <TableCell colSpan={5} align="center" sx={{ py: 3 }}>
                          <Typography color="textSecondary">
                            No incidents found
                          </Typography>
                        </TableCell>
                      </TableRow>
                    )}
                  </TableBody>
                </Table>
              </TableContainer>
            </Paper>

            {/* AI INVESTIGATION STATUS */}
            <Paper
              elevation={0}
              sx={{ p: 3, borderRadius: 3, border: "1px solid #e0e0e0" }}
            >
              <Box sx={{ display: "flex", alignItems: "center", mb: 3 }}>
                <AutoAwesome sx={{ mr: 1, color: "#7b1fa2" }} />
                <Typography variant="h6" sx={{ fontWeight: 700 }}>
                  AI Investigation Status
                </Typography>
              </Box>
              <Stack spacing={3}>
                {incidents
                  .filter((i) => i.status === "Investigating")
                  .slice(0, 5)
                  .map((item, idx) => {
                    const progress = 30 + Math.random() * 50;
                    return (
                      <Box key={item.incidentNo}>
                        <Box
                          sx={{
                            display: "flex",
                            justifyContent: "space-between",
                            mb: 1,
                          }}
                        >
                          <Typography variant="body2" sx={{ fontWeight: 600 }}>
                            {item.incidentNo} — Analyzing {item.errorType}
                          </Typography>
                          <Typography
                            variant="body2"
                            sx={{ fontWeight: "bold", color: "#7b1fa2" }}
                          >
                            {Math.round(progress)}%
                          </Typography>
                        </Box>
                        <LinearProgress
                          variant="determinate"
                          value={progress}
                          sx={{
                            height: 8,
                            borderRadius: 4,
                            bgcolor: "#f3e5f5",
                            "& .MuiLinearProgress-bar": { bgcolor: "#7b1fa2" },
                          }}
                        />
                      </Box>
                    );
                  })}
                {incidents.filter((i) => i.status === "Investigating").length === 0 && (
                  <Typography color="textSecondary" align="center">
                    No active investigations
                  </Typography>
                )}
              </Stack>
            </Paper>
          </Grid>

          {/* RIGHT COLUMN: VULNERABILITY & STATS */}
          <Grid item xs={12} md={4}>
            <Card
              elevation={0}
              sx={{
                borderRadius: 3,
                border: "1px solid #e0e0e0",
                bgcolor: "#1a2035",
                color: "white",
                mb: 3,
              }}
            >
              <CardContent sx={{ p: 3 }}>
                <Box sx={{ display: "flex", alignItems: "center", mb: 2 }}>
                  <Security sx={{ mr: 1, color: "#4caf50" }} />
                  <Typography variant="h6" sx={{ fontWeight: 600 }}>
                    Vulnerability Scan
                  </Typography>
                </Box>
                <Typography variant="body2" sx={{ opacity: 0.7, mb: 3 }}>
                  Last Scan: 10 mins ago
                </Typography>

                <Stack spacing={2}>
                  <Box
                    sx={{
                      display: "flex",
                      justifyContent: "space-between",
                      alignItems: "center",
                    }}
                  >
                    <Typography variant="body2">Critical</Typography>
                    <Chip
                      label="2"
                      size="small"
                      sx={{
                        bgcolor: "#f44336",
                        color: "white",
                        fontWeight: "bold",
                      }}
                    />
                  </Box>
                  <Box
                    sx={{
                      display: "flex",
                      justifyContent: "space-between",
                      alignItems: "center",
                    }}
                  >
                    <Typography variant="body2">High</Typography>
                    <Chip
                      label="8"
                      size="small"
                      sx={{
                        bgcolor: "#ff9800",
                        color: "white",
                        fontWeight: "bold",
                      }}
                    />
                  </Box>
                  <Box
                    sx={{
                      display: "flex",
                      justifyContent: "space-between",
                      alignItems: "center",
                    }}
                  >
                    <Typography variant="body2">Medium</Typography>
                    <Chip
                      label="21"
                      size="small"
                      sx={{
                        bgcolor: "#ffeb3b",
                        color: "#333",
                        fontWeight: "bold",
                      }}
                    />
                  </Box>
                </Stack>

                <Button
                  variant="contained"
                  fullWidth
                  sx={{
                    mt: 3,
                    bgcolor: "#4caf50",
                    "&:hover": { bgcolor: "#388e3c" },
                  }}
                >
                  Run New Scan
                </Button>
              </CardContent>
            </Card>

            <Paper
              elevation={0}
              sx={{
                p: 3,
                borderRadius: 3,
                border: "1px solid #e0e0e0",
                bgcolor: "#f8f9fa",
              }}
            >
              <Typography variant="subtitle1" sx={{ fontWeight: 700, mb: 2 }}>
                System Health
              </Typography>
              <Stack spacing={2}>
                {["API Gateway", "Auth Service", "Database Cluster"].map(
                  (service) => (
                    <Box
                      key={service}
                      sx={{ display: "flex", justifyContent: "space-between" }}
                    >
                      <Typography variant="body2">{service}</Typography>
                      <Box
                        sx={{
                          width: 10,
                          height: 10,
                          borderRadius: "50%",
                          bgcolor: "#4caf50",
                          alignSelf: "center",
                        }}
                      />
                    </Box>
                  )
                )}
              </Stack>
            </Paper>
          </Grid>
        </Grid>
      </Box>
    </MainLayout>
  );
};

export default Dashboard;
