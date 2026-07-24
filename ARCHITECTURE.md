# Application Monitoring System - Architecture

## High-Level Architecture

```
┌─────────────────────────────────────────────────────────┐
│                     React Frontend (Port 3000)           │
│  ┌──────────────────────────────────────────────────┐  │
│  │  Dashboard │ Metrics │ Logs │ Health │ Alerts    │  │
│  └──────────────────────────────────────────────────┘  │
└────────────────┬────────────────────────────────────────┘
                 │ HTTP REST API
                 │
┌────────────────▼────────────────────────────────────────┐
│          Spring Boot Backend (Port 8080)                 │
│  ┌──────────────────────────────────────────────────┐  │
│  │         REST Controllers (API Layer)              │  │
│  │  ┌─────────┬─────────┬────────┬─────────────┐   │  │
│  │  │Metrics  │  Logs   │ Health │   Alerts    │   │  │
│  │  └─────────┴─────────┴────────┴─────────────┘   │  │
│  └──────────────────────────────────────────────────┘  │
│  ┌──────────────────────────────────────────────────┐  │
│  │           Service Layer (Business Logic)          │  │
│  │  ┌─────────┬─────────┬────────┬─────────────┐   │  │
│  │  │Metric   │ Log     │Health  │   Alert     │   │  │
│  │  │Service  │Service  │Service │   Service   │   │  │
│  │  └─────────┴─────────┴────────┴─────────────┘   │  │
│  └──────────────────────────────────────────────────┘  │
│  ┌──────────────────────────────────────────────────┐  │
│  │    Repository Layer (Data Access - JPA)          │  │
│  │  ┌─────────┬─────────┬────────┬─────────────┐   │  │
│  │  │Metric   │ Log     │Health  │   Alert     │   │  │
│  │  │Repo     │Repo     │Repo    │   Repo      │   │  │
│  │  └─────────┴─────────┴────────┴─────────────┘   │  │
│  └──────────────────────────────────────────────────┘  │
└────────────────┬────────────────────────────────────────┘
                 │ JDBC
                 │
┌────────────────▼────────────────────────────────────────┐
│            PostgreSQL Database (Port 5432)               │
│  ┌────────────────────────────────────────────────┐    │
│  │ ┌─────────┬─────────┬────────┬─────────────┐ │    │
│  │ │Metrics  │ Logs    │ Health │   Alerts    │ │    │
│  │ │Table    │ Table   │ Table  │   Table     │ │    │
│  │ └─────────┴─────────┴────────┴─────────────┘ │    │
│  └────────────────────────────────────────────────┘    │
└─────────────────────────────────────────────────────────┘
```

## Backend Architecture

### 1. Controller Layer

**Responsibilities:**
- Handle HTTP requests
- Route requests to appropriate services
- Return HTTP responses
- Implement CORS configuration

**Components:**
- `MetricController` - Manages metric endpoints
- `LogController` - Manages log endpoints
- `HealthStatusController` - Manages health status endpoints
- `AlertController` - Manages alert endpoints

**Example Endpoints:**
```
POST   /api/metrics                           - Create metric
GET    /api/metrics/application/{appName}    - Get metrics by application
GET    /api/metrics/application/{appName}/type/{type}  - Get metrics by type
POST   /api/logs                              - Create log
GET    /api/logs/application/{appName}       - Get logs by application
```

### 2. Service Layer

**Responsibilities:**
- Implement business logic
- Transform between DTOs and entities
- Handle data validation
- Manage transactions

**Components:**
- `MetricService` - Metric business logic
- `LogService` - Log business logic
- `HealthStatusService` - Health status business logic
- `AlertService` - Alert business logic

**Key Operations:**
- CRUD operations for each entity
- Data filtering and sorting
- Pagination support
- Data aggregation and analysis

### 3. Repository Layer

**Responsibilities:**
- Data persistence and retrieval
- Database query execution
- JPA entity management

**Components:**
- `MetricRepository` - Metric data access
- `LogRepository` - Log data access
- `HealthStatusRepository` - Health status data access
- `AlertRepository` - Alert data access

**Database Queries:**
- Find by application name
- Find by type/level/severity
- Time-range queries
- Pagination queries

### 4. Entity Layer

**Responsibilities:**
- Define data models
- Map to database tables
- Provide ORM functionality

**Entities:**
- `Metric` - Application metrics
- `ApplicationLog` - Application logs
- `HealthStatus` - Application health status
- `Alert` - System alerts

### 5. DTO Layer

**Responsibilities:**
- Define data structures for API communication
- Separate internal entities from API contracts

**DTOs:**
- `MetricDTO` - Metric data transfer
- `LogDTO` - Log data transfer
- `HealthStatusDTO` - Health status data transfer
- `AlertDTO` - Alert data transfer

## Frontend Architecture

### 1. Pages

**Components:**
- `Dashboard.jsx` - Main dashboard overview
- `Metrics.jsx` - Metrics view and analysis
- `Logs.jsx` - Logs view with filtering
- `Health.jsx` - Health status monitoring
- `Alerts.jsx` - Alerts management

**Responsibilities:**
- Fetch data from API
- Manage page state
- Render page content
- Handle user interactions

### 2. Components (Reusable)

**Components:**
- `StatCard.jsx` - Display statistic cards
- `AlertList.jsx` - Display alerts list
- `LogTable.jsx` - Display logs in table format
- `AlertsTable.jsx` - Display alerts in table format
- `MetricsChart.jsx` - Display metrics visualization
- `HealthChart.jsx` - Display health timeline

**Responsibilities:**
- Reusable UI elements
- Data presentation
- Formatting and styling
- Event handling

### 3. Services

**Components:**
- `api.js` - API client for backend communication

**Responsibilities:**
- HTTP requests using axios
- API endpoint configuration
- Request/response handling
- Error handling

### 4. Styling

**CSS Modules:**
- `App.css` - Main app styling
- `Dashboard.css` - Dashboard styling
- `Pages.css` - Page component styling
- Component-specific CSS files

## Data Flow

### Metrics Flow

```
User Interface
    ↓
MetricController
    ↓
MetricService (Business Logic)
    ↓
MetricRepository (Data Access)
    ↓
Database (metrics table)
```

### Alert Flow

```
Alert Created
    ↓
AlertController (receives alert)
    ↓
AlertService (creates alert)
    ↓
AlertRepository (persists to DB)
    ↓
    
Alert Retrieval
    ↓
AlertController (fetch alert)
    ↓
AlertService (fetch from repository)
    ↓
AlertRepository (query database)
    ↓
Frontend (display alert)
```

## Database Schema

### Metrics Table
```sql
CREATE TABLE metrics (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(255) NOT NULL,
    value DOUBLE NOT NULL,
    unit VARCHAR(50),
    metric_type VARCHAR(50),
    application_name VARCHAR(255),
    timestamp DATETIME NOT NULL,
    tags TEXT
);
```

### Application Logs Table
```sql
CREATE TABLE application_logs (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    application_name VARCHAR(255) NOT NULL,
    level VARCHAR(20) NOT NULL,
    message TEXT NOT NULL,
    stack_trace TEXT,
    timestamp DATETIME NOT NULL,
    logger_name VARCHAR(255),
    thread_name VARCHAR(255)
);
```

### Health Status Table
```sql
CREATE TABLE health_status (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    application_name VARCHAR(255) NOT NULL,
    status VARCHAR(20) NOT NULL,
    details TEXT,
    timestamp DATETIME NOT NULL,
    last_check_time DATETIME,
    response_time_ms BIGINT
);
```

### Alerts Table
```sql
CREATE TABLE alerts (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(255) NOT NULL,
    application_name VARCHAR(255) NOT NULL,
    description TEXT,
    severity VARCHAR(20) NOT NULL,
    status VARCHAR(20) NOT NULL,
    metric_name VARCHAR(255),
    threshold DOUBLE,
    triggered_at DATETIME NOT NULL,
    resolved_at DATETIME,
    acknowledged_at DATETIME
);
```

## Design Patterns

### 1. MVC (Model-View-Controller)
- Separation of concerns
- Controllers handle requests
- Services manage business logic
- Repositories handle data access

### 2. Repository Pattern
- Data access abstraction
- Easy to test and mock
- JPA integration

### 3. Service Locator Pattern
- Dependency injection via `@RequiredArgsConstructor`
- Loose coupling between components

### 4. DTO Pattern
- API contract definition
- Internal entity isolation
- Data transformation

## Key Technologies

### Backend
- **Framework**: Spring Boot 3.1.5
- **Database**: PostgreSQL
- **ORM**: Hibernate (via Spring Data JPA)
- **Monitoring**: Micrometer, Prometheus
- **Build**: Maven

### Frontend
- **Framework**: React 18
- **Routing**: React Router v6
- **HTTP Client**: Axios
- **Styling**: CSS3
- **Date Handling**: date-fns
- **Build**: Create React App

## Security Considerations

1. **CORS Configuration**: Enabled for localhost
2. **Input Validation**: via Jakarta validation
3. **Database Security**: PostgreSQL with credentials
4. **API Protection**: Should add authentication/authorization

## Scalability Considerations

1. **Horizontal Scaling**: Stateless services
2. **Database**: Connection pooling via Spring Boot
3. **Caching**: Can add Redis/Memcached
4. **Load Balancing**: Use Nginx/HAProxy
5. **Message Queue**: Consider RabbitMQ/Kafka for async operations

## Performance Optimization

1. **Pagination**: Implemented for large datasets
2. **Indexing**: Add indexes on frequently queried columns
3. **Lazy Loading**: Consider for related entities
4. **Caching**: Add spring-cache for frequently accessed data
5. **Query Optimization**: Use efficient JPA queries

## Monitoring & Logging

1. **Actuator Endpoints**: Health, metrics, Prometheus
2. **Spring Boot Logging**: Configured in application.properties
3. **Application Metrics**: Available at `/api/management/metrics`
4. **Prometheus Support**: Available at `/api/management/prometheus`

## Future Enhancements

1. Add authentication/authorization (JWT)
2. Implement real-time updates (WebSocket)
3. Add alert rules engine
4. Integrate with message brokers
5. Add export functionality (CSV/PDF)
6. Implement audit logging
7. Add multi-tenancy support
8. Implement caching layer
