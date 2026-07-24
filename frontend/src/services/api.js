import axios from 'axios';

const API_BASE_URL = 'http://localhost:8080/api';

const apiClient = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    'Content-Type': 'application/json',
  },
});

// Metrics API
export const getMetricsByApplication = (appName, page = 0, size = 20) =>
  apiClient.get(`/metrics/application/${appName}?page=${page}&size=${size}`);

export const getMetricsByType = (appName, type, page = 0, size = 20) =>
  apiClient.get(`/metrics/application/${appName}/type/${type}?page=${page}&size=${size}`);

export const getRecentMetrics = (appName, since) =>
  apiClient.get(`/metrics/application/${appName}/recent?since=${since}`);

export const createMetric = (metricData) =>
  apiClient.post('/metrics', metricData);

// Logs API
export const getLogsByApplication = (appName, page = 0, size = 20) =>
  apiClient.get(`/logs/application/${appName}?page=${page}&size=${size}`);

export const getLogsByLevel = (appName, level, page = 0, size = 20) =>
  apiClient.get(`/logs/application/${appName}/level/${level}?page=${page}&size=${size}`);

export const createLog = (logData) =>
  apiClient.post('/logs', logData);

// Health Status API
export const getHealthStatus = (appName) =>
  apiClient.get(`/health/application/${appName}`);

export const getHealthHistory = (appName) =>
  apiClient.get(`/health/application/${appName}/history`);

export const getAllHealthStatus = () =>
  apiClient.get('/health/all');

export const recordHealthStatus = (healthData) =>
  apiClient.post('/health', healthData);

// Alerts API
export const getAlertsByApplication = (appName, page = 0, size = 20) =>
  apiClient.get(`/alerts/application/${appName}?page=${page}&size=${size}`);

export const getActiveAlerts = (appName) =>
  apiClient.get(`/alerts/application/${appName}/active`);

export const getAlertsBySeverity = (appName, severity, page = 0, size = 20) =>
  apiClient.get(`/alerts/application/${appName}/severity/${severity}?page=${page}&size=${size}`);

export const createAlert = (alertData) =>
  apiClient.post('/alerts', alertData);

export const acknowledgeAlert = (alertId) =>
  apiClient.put(`/alerts/${alertId}/acknowledge`);

export const resolveAlert = (alertId) =>
  apiClient.put(`/alerts/${alertId}/resolve`);

export default apiClient;
