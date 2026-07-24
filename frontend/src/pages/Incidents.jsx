import React, { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import {
  Box,
  Typography,
  Button,
  Paper,
  Stack,
  TextField,
  InputAdornment,
  Chip,
  IconButton,
  Tooltip,
  LinearProgress,
  CircularProgress,
} from "@mui/material";
import MainLayout from "./MainLayout";
import { DataGrid, GridToolbarContainer, GridPagination } from "@mui/x-data-grid";

import SearchIcon from "@mui/icons-material/Search";
import AddIcon from "@mui/icons-material/Add";
import RefreshIcon from "@mui/icons-material/Refresh";
import FileDownloadIcon from "@mui/icons-material/FileDownload";
import VisibilityIcon from "@mui/icons-material/Visibility";
import CircleIcon from "@mui/icons-material/Circle";
import FilterListIcon from "@mui/icons-material/FilterList";

const Incidents = () => {
  const navigate = useNavigate();
  const [incidents, setIncidents] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [searchTerm, setSearchTerm] = useState("");
  const [paginationModel, setPaginationModel] = useState({
    pageSize: 20,
    page: 0,
  });

  useEffect(() => {
    fetchIncidents();
  }, []);

  const fetchIncidents = async () => {
    try {
      setLoading(true);
      let allIncidents = [];
      let page = 0;
      let hasMore = true;

      // Fetch all pages
      while (hasMore) {
        const response = await fetch(`http://localhost:8080/api/incidents?page=${page}&size=50`);
        if (!response.ok) throw new Error("Failed to fetch incidents");
        const data = await response.json();
        allIncidents = [...allIncidents, ...(data.content || [])];
        hasMore = !data.last;
        page++;
      }

      setIncidents(allIncidents);
      setError(null);
    } catch (err) {
      setError(err.message);
      console.error("Error fetching incidents:", err);
    } finally {
      setLoading(false);
    }
  };

  const transformIncidents = () => {
    return incidents
      .map((inc) => {
        let formattedDate = "N/A";
        if (inc.createdAt) {
          try {
            const date = new Date(inc.createdAt);
            if (!isNaN(date.getTime())) {
              formattedDate = date.toLocaleString("en-US", {
                year: "numeric",
                month: "short",
                day: "numeric",
                hour: "2-digit",
                minute: "2-digit",
                second: "2-digit"
              });
            }
          } catch (e) {
            console.error("Date parsing error:", inc.createdAt, e);
          }
        }
        return {
          id: inc.incidentNo,
          incidentNo: inc.incidentNo,
          title: inc.description || inc.errorType,
          severity: inc.severity,
          status: inc.status,
          aiScore: Math.floor(Math.random() * 40 + 60),
          createdAt: formattedDate,
          createdBy: inc.createdBy,
          duration: "N/A",
          environment: inc.environment,
          application: inc.applicationName,
        };
      })
      .filter(
        (inc) =>
          !searchTerm ||
          inc.incidentNo.toLowerCase().includes(searchTerm.toLowerCase()) ||
          inc.title.toLowerCase().includes(searchTerm.toLowerCase())
      );
  };

  // --- COLUMN DEFINITIONS ---
  const columns = [
    {
      field: "id",
      headerName: "Incident ID",
      width: 120,
      // FIX: Use display flex and alignItems center to ensure vertical centering
      renderCell: (params) => (
        <Box sx={{ display: "flex", alignItems: "center", height: "100%" }}>
          <Typography
            variant="body2"
            sx={{ color: "primary.main", fontWeight: 700, cursor: "pointer" }}
            onClick={() => navigate(`/incident/${params.value}`)}
          >
            {params.value}
          </Typography>
        </Box>
      ),
    },
    {
      field: "title",
      headerName: "Title",
      flex: 1,
      minWidth: 250,
      renderCell: (params) => (
        <Box sx={{ display: "flex", alignItems: "center", height: "100%" }}>
          <Typography variant="body2" sx={{ fontWeight: 500 }}>
            {params.value}
          </Typography>
        </Box>
      ),
    },
    {
      field: "severity",
      headerName: "Severity",
      width: 130,
      renderCell: (params) => {
        const severityKey = params.value?.toUpperCase() || "LOW";
        const config = {
          CRITICAL: { color: "#d32f2f", label: "CRITICAL" },
          HIGH: { color: "#ef6c00", label: "HIGH" },
          MEDIUM: { color: "#f9a825", label: "MEDIUM" },
          LOW: { color: "#2e7d32", label: "LOW" },
        }[severityKey] || { color: "#666", label: severityKey };
        return (
          <Box
            sx={{
              display: "flex",
              alignItems: "center",
              gap: 1,
              height: "100%",
            }}
          >
            <CircleIcon sx={{ fontSize: 10, color: config.color }} />
            <Typography
              variant="caption"
              sx={{ fontWeight: 700, color: config.color }}
            >
              {config.label}
            </Typography>
          </Box>
        );
      },
    },
    {
      field: "status",
      headerName: "Status",
      width: 160,
      renderCell: (params) => {
        const statusKey = params.value?.toUpperCase() || "OPEN";
        const styles = {
          OPEN: { color: "info", label: "Open" },
          INVESTIGATING: { color: "warning", label: "Investigating" },
          RESOLVED: { color: "success", label: "Resolved" },
          FAILED: { color: "error", label: "Failed" },
        }[statusKey] || { color: "default", label: params.value };

        return (
          <Box sx={{ display: "flex", alignItems: "center", height: "100%" }}>
            <Chip
              label={styles.label}
              size="small"
              color={styles.color}
              sx={{ fontWeight: 600, fontSize: "0.7rem" }}
            />
          </Box>
        );
      },
    },
    {
      field: "aiScore",
      headerName: "AI Confidence",
      width: 160,
      renderCell: (params) => (
        // FIX: Use Column Flex + Center Justify for vertical centering of multi-line content
        <Box
          sx={{
            width: "100%",
            height: "100%",
            display: "flex",
            flexDirection: "column",
            justifyContent: "center",
            pr: 2,
          }}
        >
          <Stack direction="row" justifyContent="space-between" mb={0.5}>
            <Typography variant="caption" sx={{ fontWeight: 700 }}>
              {params.value}%
            </Typography>
          </Stack>
          <LinearProgress
            variant="determinate"
            value={params.value}
            sx={{ height: 6, borderRadius: 3, bgcolor: "#eee" }}
          />
        </Box>
      ),
    },
    {
      field: "createdAt",
      headerName: "Created At",
      width: 140,
      renderCell: (params) => (
        // FIX: Ensure vertical centering for two-line text
        <Box
          sx={{
            display: "flex",
            flexDirection: "column",
            justifyContent: "center",
            height: "100%",
          }}
        >
          <Typography variant="body2" sx={{ lineHeight: 1 }}>
            {params.value}
          </Typography>
          <Typography variant="caption" color="textSecondary">
            {params.row.createdBy}
          </Typography>
        </Box>
      ),
    },
    {
      field: "actions",
      headerName: "Actions",
      width: 120,
      sortable: false,
      renderCell: (params) => (
        // FIX: align-items center for icon buttons
        <Stack
          direction="row"
          spacing={0.5}
          sx={{ height: "100%", alignItems: "center" }}
        >
          <IconButton
            size="small"
            onClick={() => navigate(`/incident/${params.row.id}`)}
          >
            <VisibilityIcon fontSize="small" />
          </IconButton>
        </Stack>
      ),
    },
  ];
  const CustomToolbar = () => (
    <GridToolbarContainer
      sx={{
        p: 2,
        display: "flex",
        justifyContent: "space-between",
        borderBottom: "1px solid #e0e0e0",
      }}
    >
      <Stack direction="row" spacing={2} alignItems="center">
        <TextField
          size="small"
          placeholder="Search Incident..."
          value={searchTerm}
          onChange={(e) => setSearchTerm(e.target.value)}
          InputProps={{
            startAdornment: (
              <InputAdornment position="start">
                <SearchIcon fontSize="small" />
              </InputAdornment>
            ),
          }}
          sx={{ width: 300, bgcolor: "white" }}
        />
        <Button size="small" startIcon={<FilterListIcon />} color="inherit">
          Severity
        </Button>
        <Button size="small" startIcon={<FilterListIcon />} color="inherit">
          Status
        </Button>
      </Stack>
      <Stack direction="row" spacing={1}>
        <Tooltip title="Refresh">
          <IconButton size="small" onClick={fetchIncidents}>
            <RefreshIcon />
          </IconButton>
        </Tooltip>
        <Button size="small" startIcon={<FileDownloadIcon />} color="inherit">
          Export
        </Button>
        <Button
          variant="contained"
          startIcon={<AddIcon />}
          onClick={() => navigate("/investigate")}
          sx={{ bgcolor: "#1a2035", textTransform: "none", fontWeight: 600 }}
        >
          New Investigation
        </Button>
      </Stack>
    </GridToolbarContainer>
  );

  if (loading) {
    return (
      <MainLayout>
        <Box sx={{ display: "flex", justifyContent: "center", alignItems: "center", height: "400px" }}>
          <CircularProgress />
        </Box>
      </MainLayout>
    );
  }

  return (
    <MainLayout>
      <Box sx={{ p: { xs: 2, md: 4 }, height: "100%" }}>
        {error && (
          <Box sx={{ mb: 2, p: 2, bgcolor: "#ffebee", borderRadius: 1 }}>
            <Typography color="error">⚠️ Error: {error}</Typography>
          </Box>
        )}

        {/* Page Header */}
        <Stack
          direction="row"
          justifyContent="space-between"
          alignItems="center"
          sx={{ mb: 3 }}
        >
          <Box>
            <Typography variant="h4" sx={{ fontWeight: 800 }}>
              Incident Management
            </Typography>
            <Typography variant="body2" color="textSecondary">
              {transformIncidents().length} incidents found
            </Typography>
          </Box>
        </Stack>

        {/* Main DataGrid Table */}
        <Paper
          elevation={0}
          sx={{
            borderRadius: 3,
            border: "1px solid #e0e0e0",
            overflow: "hidden",
          }}
        >
          <DataGrid
            rows={transformIncidents()}
            columns={columns}
            autoHeight
            checkboxSelection
            disableSelectionOnClick
            paginationModel={paginationModel}
            onPaginationModelChange={setPaginationModel}
            pageSizeOptions={[10, 20, 50]}
            slots={{ toolbar: CustomToolbar }}
            sx={{
              border: "none",
              "& .MuiDataGrid-columnHeaders": {
                bgcolor: "#f8f9fa",
                color: "text.secondary",
                fontWeight: 700,
                fontSize: "0.75rem",
                textTransform: "uppercase",
              },
              "& .MuiDataGrid-cell:focus": {
                outline: "none",
              },
              "& .MuiDataGrid-row:hover": {
                bgcolor: "#f0f4f8",
                cursor: "pointer",
              },
            }}
          />
        </Paper>
      </Box>
    </MainLayout>
  );
};

export default Incidents;
