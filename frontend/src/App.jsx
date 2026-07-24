import React from "react";
import { BrowserRouter, Routes, Route, Navigate } from "react-router-dom";
import Dashboard from "./pages/Dashboard";
import IncidentDetail from "./components/IncidentDetail";
import Investigate from "./pages/Investigate";
import Incidents from "./pages/Incidents";
import VulnerabilityDetails from "./pages/VulnerabilityDetails";

function App() {
  return (
    <BrowserRouter>
      <Routes>
        {/* All routes inside this Route will share the Sidebar and Header */}
        {/* Redirect root to dashboard */}
        <Route index element={<Navigate to="/dashboard" replace />} />

        <Route path="dashboard" element={<Dashboard />} />
        <Route path="investigate" element={<Investigate />} />
        <Route path="incidents" element={<Incidents />} />
        <Route path="vulnerabilities" element={<VulnerabilityDetails />} />

        {/* Dynamic route for incident details */}
        <Route path="incident/:id" element={<IncidentDetail />} />
      </Routes>
    </BrowserRouter>
  );
}

export default App;
