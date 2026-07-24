import React, { useState } from "react";
import { useNavigate, useLocation, Outlet } from "react-router-dom";
import {
  AppBar,
  Box,
  CssBaseline,
  Divider,
  Drawer,
  IconButton,
  List,
  ListItem,
  ListItemButton,
  ListItemIcon,
  ListItemText,
  Toolbar,
  Typography,
  Avatar,
  Menu,
  MenuItem,
  Paper,
  Breadcrumbs,
  Link as MuiLink,
} from "@mui/material";
import {
  Menu as MenuIcon,
  Dashboard as DashboardIcon,
  Troubleshoot as InvestigateIcon,
  ListAlt as IncidentsIcon,
  Logout as LogoutIcon,
  AccountCircle,
  Settings as SettingsIcon,
  Security as SecurityIcon,
  NotificationsNone as NotificationsIcon,
  Hub as LogoIcon,
  NavigateNext as NavigateNextIcon,
} from "@mui/icons-material";

const drawerWidth = 260;

const MainLayout = ({ children }) => {
  const navigate = useNavigate();
  const location = useLocation();
  const [mobileOpen, setMobileOpen] = useState(false);
  const [anchorEl, setAnchorEl] = useState(null);

  const handleDrawerToggle = () => setMobileOpen(!mobileOpen);
  const handleMenuOpen = (event) => setAnchorEl(event.currentTarget);
  const handleMenuClose = () => setAnchorEl(null);

  // --- MENU CONFIGURATION ---
  const menuItems = [
    { text: "Dashboard", icon: <DashboardIcon />, path: "/dashboard" },
    { text: "Investigate", icon: <InvestigateIcon />, path: "/investigate" },
    { text: "Incidents", icon: <IncidentsIcon />, path: "/incidents" },
    {
      text: "Vulnerbilities Report",
      icon: <SecurityIcon />,
      path: "/vulnerabilities",
    },
  ];

  // --- DYNAMIC BREADCRUMB LOGIC ---
  const generateBreadcrumbs = () => {
    const pathnames = location.pathname.split("/").filter((x) => x);

    return (
      <Breadcrumbs
        separator={<NavigateNextIcon fontSize="small" />}
        aria-label="breadcrumb"
        sx={{ ml: 2, display: { xs: "none", md: "block" } }}
      >
        <MuiLink
          underline="hover"
          color="inherit"
          onClick={() => navigate("/dashboard")}
          sx={{ cursor: "pointer", fontSize: "0.85rem" }}
        >
          OpsBeacon
        </MuiLink>
        {pathnames.map((value, index) => {
          const last = index === pathnames.length - 1;
          const to = `/${pathnames.slice(0, index + 1).join("/")}`;
          const label = value.charAt(0).toUpperCase() + value.slice(1);

          return last ? (
            <Typography
              color="text.primary"
              key={to}
              sx={{ fontSize: "0.85rem", fontWeight: 600 }}
            >
              {label}
            </Typography>
          ) : (
            <MuiLink
              underline="hover"
              color="inherit"
              onClick={() => navigate(to)}
              key={to}
              sx={{ cursor: "pointer", fontSize: "0.85rem" }}
            >
              {label}
            </MuiLink>
          );
        })}
      </Breadcrumbs>
    );
  };

  const drawer = (
    <Box sx={{ height: "100%", display: "flex", flexDirection: "column" }}>
      <Toolbar sx={{ px: [2], display: "flex", alignItems: "center", gap: 1 }}>
        <LogoIcon sx={{ color: "primary.main", fontSize: 32 }} />
        <Typography
          variant="h6"
          sx={{
            fontWeight: 800,
            letterSpacing: "-0.5px",
            color: "text.primary",
          }}
        >
          OpsBeacon
        </Typography>
      </Toolbar>
      <Divider sx={{ opacity: 0.6 }} />

      <List sx={{ px: 1.5, py: 2 }}>
        {menuItems.map((item) => {
          // Dynamic Active State Check
          const isActive = location.pathname.startsWith(item.path);

          return (
            <ListItem key={item.text} disablePadding sx={{ mb: 0.5 }}>
              <ListItemButton
                onClick={() => {
                  navigate(item.path);
                  setMobileOpen(false);
                }}
                sx={{
                  borderRadius: "8px",
                  backgroundColor: isActive
                    ? "rgba(25, 118, 210, 0.08)"
                    : "transparent",
                  color: isActive ? "primary.main" : "text.secondary",
                  "&:hover": {
                    backgroundColor: isActive
                      ? "rgba(25, 118, 210, 0.12)"
                      : "rgba(0, 0, 0, 0.04)",
                  },
                }}
              >
                <ListItemIcon
                  sx={{
                    color: isActive ? "primary.main" : "inherit",
                    minWidth: 40,
                  }}
                >
                  {item.icon}
                </ListItemIcon>
                <ListItemText
                  primary={item.text}
                  primaryTypographyProps={{
                    fontSize: "0.9rem",
                    fontWeight: isActive ? 700 : 500,
                  }}
                />
              </ListItemButton>
            </ListItem>
          );
        })}
      </List>

      <Box sx={{ mt: "auto", p: 2 }}>
        <Paper
          variant="outlined"
          sx={{
            p: 2,
            bgcolor: "#f8f9fa",
            borderRadius: 2,
            borderStyle: "dashed",
          }}
        >
          <Typography
            variant="caption"
            color="textSecondary"
            sx={{ fontWeight: "bold" }}
          >
            SYSTEM STATUS
          </Typography>
          <Box sx={{ display: "flex", alignItems: "center", mt: 1 }}>
            <Box
              sx={{
                width: 8,
                height: 8,
                bgcolor: "#4caf50",
                borderRadius: "50%",
                mr: 1,
              }}
            />
            <Typography variant="caption">All Agents Operational</Typography>
          </Box>
        </Paper>
      </Box>
    </Box>
  );

  return (
    <Box sx={{ display: "flex" }}>
      <CssBaseline />

      {/* --- HEADER --- */}
      <AppBar
        position="fixed"
        elevation={0}
        sx={{
          width: { sm: `calc(100% - ${drawerWidth}px)` },
          ml: { sm: `${drawerWidth}px` },
          bgcolor: "white",
          borderBottom: "1px solid #e0e0e0",
          color: "text.primary",
          zIndex: (theme) => theme.zIndex.drawer + 1,
        }}
      >
        <Toolbar sx={{ justifyContent: "space-between" }}>
          <Box sx={{ display: "flex", alignItems: "center" }}>
            <IconButton
              color="inherit"
              edge="start"
              onClick={handleDrawerToggle}
              sx={{ mr: 2, display: { sm: "none" } }}
            >
              <MenuIcon />
            </IconButton>

            {/* --- DYNAMIC BREADCRUMBS --- */}
            {generateBreadcrumbs()}
          </Box>

          <Box sx={{ display: "flex", alignItems: "center", gap: 1 }}>
            <IconButton size="large" color="inherit">
              <NotificationsIcon fontSize="small" />
            </IconButton>

            <Divider
              orientation="vertical"
              flexItem
              sx={{ mx: 1, height: 24, alignSelf: "center" }}
            />

            <Box
              sx={{ display: "flex", alignItems: "center", cursor: "pointer" }}
              onClick={handleMenuOpen}
            >
              <Box
                sx={{
                  textAlign: "right",
                  mr: 1.5,
                  display: { xs: "none", sm: "block" },
                }}
              >
                <Typography
                  variant="body2"
                  sx={{ fontWeight: 600, lineHeight: 1.2 }}
                >
                  Alex Rivera
                </Typography>
                <Typography variant="caption" color="textSecondary">
                  SRE Lead
                </Typography>
              </Box>
              <Avatar
                sx={{
                  width: 36,
                  height: 36,
                  bgcolor: "primary.main",
                  fontSize: "1rem",
                  fontWeight: 600,
                }}
              >
                AR
              </Avatar>
            </Box>

            <Menu
              anchorEl={anchorEl}
              open={Boolean(anchorEl)}
              onClose={handleMenuClose}
              transformOrigin={{ horizontal: "right", vertical: "top" }}
              anchorOrigin={{ horizontal: "right", vertical: "bottom" }}
              PaperProps={{
                elevation: 3,
                sx: { mt: 1.5, minWidth: 180, borderRadius: 2 },
              }}
            >
              <MenuItem onClick={handleMenuClose}>
                <ListItemIcon>
                  <AccountCircle fontSize="small" />
                </ListItemIcon>
                Profile
              </MenuItem>
              <MenuItem onClick={handleMenuClose}>
                <ListItemIcon>
                  <SettingsIcon fontSize="small" />
                </ListItemIcon>
                Settings
              </MenuItem>
              <Divider />
              <MenuItem onClick={handleMenuClose} sx={{ color: "error.main" }}>
                <ListItemIcon>
                  <LogoutIcon fontSize="small" color="error" />
                </ListItemIcon>
                Logout
              </MenuItem>
            </Menu>
          </Box>
        </Toolbar>
      </AppBar>

      {/* --- SIDEBAR --- */}
      <Box
        component="nav"
        sx={{ width: { sm: drawerWidth }, flexShrink: { sm: 0 } }}
      >
        <Drawer
          variant="temporary"
          open={mobileOpen}
          onClose={handleDrawerToggle}
          ModalProps={{ keepMounted: true }}
          sx={{
            display: { xs: "block", sm: "none" },
            "& .MuiDrawer-paper": {
              boxSizing: "border-box",
              width: drawerWidth,
            },
          }}
        >
          {drawer}
        </Drawer>
        <Drawer
          variant="permanent"
          sx={{
            display: { xs: "none", sm: "block" },
            "& .MuiDrawer-paper": {
              boxSizing: "border-box",
              width: drawerWidth,
              borderRight: "1px solid #e0e0e0",
              bgcolor: "#ffffff",
            },
          }}
          open
        >
          {drawer}
        </Drawer>
      </Box>

      {/* --- MAIN CONTENT AREA --- */}
      <Box
        component="main"
        sx={{
          flexGrow: 1,
          p: 0,
          width: { sm: `calc(100% - ${drawerWidth}px)` },
          minHeight: "100vh",
          bgcolor: "#f4f7f9",
        }}
      >
        <Toolbar />
        {/* If using React Router, Outlet will render the child routes here */}
        {children || <Outlet />}
      </Box>
    </Box>
  );
};

export default MainLayout;
