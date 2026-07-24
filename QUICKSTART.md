# Quick Start Guide

Get the Application Monitoring System up and running in 5 minutes!

## Option 1: Using Docker (Recommended)

### Prerequisites
- Docker
- Docker Compose
- Java 17+
- Node.js 16+

### Steps

1. **Start PostgreSQL**
   ```bash
   docker-compose up -d
   ```

2. **Start Backend**
   ```bash
   cd backend
   mvn spring-boot:run
   ```
   Backend will be available at: `http://localhost:8080/api`

3. **Start Frontend**
   ```bash
   cd frontend
   npm install
   npm start
   ```
   Frontend will open at: `http://localhost:3000`

## Option 2: Local Setup

### Prerequisites
- PostgreSQL 12+ (running locally)
- Java 17+
- Node.js 16+
- Maven 3.6+

### Steps

1. **Create Database**
   ```bash
   createdb monitoring_db
   ```

2. **Configure Backend**
   - Update `backend/src/main/resources/application.properties`
   - Set database credentials

3. **Start Backend**
   ```bash
   cd backend
   mvn spring-boot:run
   ```

4. **Start Frontend**
   ```bash
   cd frontend
   npm install
   npm start
   ```

## First Steps After Setup

### 1. Verify Backend is Running
```bash
curl http://localhost:8080/api/management/health
```

### 2. Add Sample Data

Create a test metric:
```bash
curl -X POST http://localhost:8080/api/metrics \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Test Metric",
    "value": 50.0,
    "unit": "%",
    "type": "CPU",
    "applicationName": "sample-app"
  }'
```

Record health status:
```bash
curl -X POST http://localhost:8080/api/health \
  -H "Content-Type: application/json" \
  -d '{
    "applicationName": "sample-app",
    "status": "UP",
    "responseTimeMs": 100
  }'
```

### 3. Access the Dashboard
- Open `http://localhost:3000` in your browser
- Select "sample-app" from the application dropdown
- View metrics, logs, health, and alerts

## Troubleshooting

### Port Already in Use
- Backend: Change port in `application.properties` (server.port)
- Frontend: Set PORT environment variable: `PORT=3001 npm start`

### Database Connection Error
- Ensure PostgreSQL is running
- Verify credentials in `application.properties`
- Check if database `monitoring_db` exists

### CORS Errors
- Frontend and backend must be on different ports
- CORS is configured in `MonitoringApplication.java`

### Node Modules Issue
```bash
cd frontend
rm -rf node_modules package-lock.json
npm install
```

## Next Steps

1. **Explore the UI**
   - Dashboard: Overview of all applications
   - Metrics: View and analyze performance metrics
   - Logs: Search and filter application logs
   - Health: Monitor application health status
   - Alerts: Create and manage alerts

2. **Configure Applications**
   - Add more applications in the dropdown
   - Start sending metrics from your applications

3. **Advanced Setup**
   - Configure alert rules
   - Set up email notifications
   - Integrate with external systems

## Documentation

See [README.md](README.md) for comprehensive documentation including:
- Architecture details
- API endpoints
- Database schema
- Configuration options
- Deployment guides

## Support

For issues or questions, open an issue in the repository.
