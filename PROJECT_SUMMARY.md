# Application Monitoring System - Project Summary

## Overview

A complete **application monitoring solution** with:
- **Spring Boot Backend** for REST APIs and data management
- **React Frontend** for modern, interactive user interface
- **PostgreSQL Database** for reliable data persistence
- **Real-time monitoring** of metrics, logs, health, and alerts

## What's Included

### Backend (Spring Boot 3.1.5)
✅ RESTful API endpoints for all operations
✅ 4 main entities: Metrics, Logs, Health Status, Alerts
✅ Service layer with business logic
✅ JPA repositories for data access
✅ CORS configuration for frontend integration
✅ Actuator endpoints for monitoring
✅ Prometheus metrics support

**Controllers:**
- MetricController - Manage application metrics
- LogController - Centralized log collection
- HealthStatusController - Monitor app health
- AlertController - Alert creation and management

**Database Features:**
- PostgreSQL with Hibernate ORM
- 4 main tables: metrics, application_logs, health_status, alerts
- Proper indexing and relationships
- Timestamps for all records

### Frontend (React 18)
✅ Modern, responsive UI with Tailwind CSS styling
✅ 5 main pages: Dashboard, Metrics, Logs, Health, Alerts
✅ Real-time data updates (auto-refresh)
✅ Advanced filtering and pagination
✅ Interactive charts and visualizations
✅ Responsive design for all devices

**Features:**
- Dashboard with status overview
- Metrics tracking and analysis
- Centralized log viewer with filtering
- Health status monitoring with history
- Alert management with status updates
- Application selector for multi-app monitoring

## File Structure

```
AI-IncidentInvestigator/
├── backend/
│   ├── pom.xml
│   ├── src/main/
│   │   ├── java/com/monitoring/
│   │   │   ├── controller/        (4 files)
│   │   │   ├── service/           (4 files)
│   │   │   ├── entity/            (4 files)
│   │   │   ├── repository/        (4 files)
│   │   │   ├── dto/               (4 files)
│   │   │   └── MonitoringApplication.java
│   │   └── resources/
│   │       └── application.properties
│   └── pom.xml
├── frontend/
│   ├── package.json
│   ├── public/index.html
│   ├── src/
│   │   ├── pages/          (5 pages)
│   │   ├── components/     (6 components)
│   │   ├── services/       (api.js)
│   │   ├── App.jsx
│   │   ├── index.js
│   │   └── CSS files
│   └── .env.example
├── scripts/
│   ├── test-api.sh
│   └── api-collection.postman.json
├── docker-compose.yml
├── README.md
├── QUICKSTART.md
├── ARCHITECTURE.md
├── PROJECT_SUMMARY.md
└── .gitignore
```

## Quick Start

### Prerequisites
- Java 17+
- Node.js 16+
- Docker & Docker Compose (or PostgreSQL 12+)

### 3-Step Setup

1. **Start Database**
   ```bash
   docker-compose up -d
   ```

2. **Start Backend**
   ```bash
   cd backend
   mvn spring-boot:run
   ```

3. **Start Frontend**
   ```bash
   cd frontend
   npm install
   npm start
   ```

Access at: `http://localhost:3000`

## Key Features

### 1. Metrics Monitoring
- Track CPU, Memory, Disk usage, Response times
- Filter by type and time range
- View statistics (min, max, average)
- Real-time updates every 10 seconds

### 2. Log Aggregation
- Centralized log collection
- Filter by log level (DEBUG, INFO, WARN, ERROR)
- Expandable stack traces
- Pagination support

### 3. Health Status
- Real-time application health
- Historical health timeline
- Response time tracking
- Status badges (UP, DOWN, DEGRADED)

### 4. Alert Management
- Create alerts with thresholds
- Multiple severity levels (INFO, WARNING, CRITICAL)
- Alert lifecycle (ACTIVE → ACKNOWLEDGED → RESOLVED)
- Summary statistics dashboard

### 5. Multi-Application Support
- Monitor multiple applications
- Application selector in dashboard
- Per-application filtering
- Centralized overview

## API Endpoints

### Metrics
- `POST /api/metrics` - Create metric
- `GET /api/metrics/application/{appName}` - Get metrics
- `GET /api/metrics/application/{appName}/type/{type}` - Filter by type
- `GET /api/metrics/application/{appName}/range` - Date range query

### Logs
- `POST /api/logs` - Create log
- `GET /api/logs/application/{appName}` - Get logs
- `GET /api/logs/application/{appName}/level/{level}` - Filter by level
- `GET /api/logs/application/{appName}/range` - Date range query

### Health
- `POST /api/health` - Record health status
- `GET /api/health/application/{appName}` - Latest status
- `GET /api/health/application/{appName}/history` - Status history
- `GET /api/health/all` - All applications health

### Alerts
- `POST /api/alerts` - Create alert
- `GET /api/alerts/application/{appName}` - Get alerts
- `GET /api/alerts/application/{appName}/active` - Get active alerts
- `GET /api/alerts/application/{appName}/severity/{severity}` - Filter by severity
- `PUT /api/alerts/{alertId}/acknowledge` - Acknowledge alert
- `PUT /api/alerts/{alertId}/resolve` - Resolve alert

### Monitoring
- `GET /api/management/health` - Application health
- `GET /api/management/metrics` - Available metrics
- `GET /api/management/prometheus` - Prometheus format metrics

## Technology Stack

### Backend
| Component | Technology | Version |
|-----------|-----------|---------|
| Framework | Spring Boot | 3.1.5 |
| Database | PostgreSQL | 12+ |
| ORM | Hibernate | Via JPA |
| Build | Maven | 3.6+ |
| Java | OpenJDK | 17+ |
| Monitoring | Micrometer | Latest |

### Frontend
| Component | Technology | Version |
|-----------|-----------|---------|
| Framework | React | 18.2.0 |
| Routing | React Router | 6.18.0 |
| HTTP | Axios | 1.5.0 |
| Styling | CSS3 | Latest |
| Date Handling | date-fns | 2.30.0 |

## Database Design

### Metrics Table
- Stores application performance metrics
- 8 columns + indexes on application_name, type, timestamp

### Application Logs Table
- Centralized log storage
- 8 columns + indexes on application_name, level, timestamp

### Health Status Table
- Health check records
- 7 columns + indexes on application_name, timestamp

### Alerts Table
- Alert lifecycle management
- 11 columns + indexes on application_name, severity, status

## Configuration

### Backend Configuration
- Server port: 8080
- Context path: /api
- Database: PostgreSQL on port 5432
- CORS enabled for localhost:3000
- Actuator endpoints enabled

### Frontend Configuration
- Dev server port: 3000
- API base URL: http://localhost:8080/api
- Auto-refresh intervals configured

## Running Tests

### Test Backend API
```bash
# Using the provided test script
bash scripts/test-api.sh

# Or import Postman collection
# scripts/api-collection.postman.json
```

### Test Frontend
```bash
cd frontend
npm test
```

## Documentation

### Files
1. **README.md** - Comprehensive documentation
2. **QUICKSTART.md** - Get started in 5 minutes
3. **ARCHITECTURE.md** - System design and architecture
4. **PROJECT_SUMMARY.md** - This file

### Resources
- Postman collection: `scripts/api-collection.postman.json`
- Test script: `scripts/test-api.sh`
- Docker Compose: `docker-compose.yml`

## Production Deployment

### Docker Support
- Backend: Create Dockerfile with OpenJDK 17
- Frontend: Build static files and serve via Nginx
- Database: Use managed PostgreSQL service

### Scalability
- Horizontal scaling: Stateless services
- Load balancing: Nginx/HAProxy
- Caching: Redis for frequently accessed data
- Monitoring: Prometheus for metrics

### Security
- Add JWT authentication
- HTTPS/TLS for all connections
- Database credentials management
- Input validation and sanitization

## Future Enhancements

### Phase 1
- [ ] User authentication (JWT)
- [ ] Role-based access control
- [ ] Alert notifications (Email, Slack)

### Phase 2
- [ ] Real-time updates (WebSocket)
- [ ] Advanced alerting rules engine
- [ ] Custom dashboards
- [ ] Data export (CSV, PDF)

### Phase 3
- [ ] Time-series database (InfluxDB, Prometheus)
- [ ] GraphQL API
- [ ] Mobile app
- [ ] Multi-tenancy support

## Support & Contribution

For issues, questions, or contributions:
1. Check existing issues
2. Review documentation
3. Create detailed bug reports
4. Submit pull requests

## License

MIT License - Feel free to use and modify

## Summary Statistics

- **Java Files**: 16 (controllers, services, entities, repos, DTOs, main app)
- **React Components**: 12 (5 pages + 6 reusable components)
- **CSS Files**: 10 (styling for all components)
- **Database Tables**: 4
- **API Endpoints**: 20+
- **Lines of Code**: ~4,000

## Getting Help

1. **Quick Start**: See `QUICKSTART.md`
2. **Architecture Details**: See `ARCHITECTURE.md`
3. **API Documentation**: See `README.md`
4. **Example Requests**: See `scripts/api-collection.postman.json`

---

**Project Created**: 2026
**Status**: Ready for Development
**Last Updated**: 2026-07-23
