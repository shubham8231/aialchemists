# Cloud SQL PostgreSQL Connection Setup

Your database credentials have been registered:
- **Instance**: hack-team-aialchemists-2026:asia-south1:alchemists-postgre
- **User**: alchmists_user
- **Password**: Alchmists_pass5
- **Database**: monitoring_db

## Option 1: Using Cloud SQL Proxy (Recommended)

### 1. Install Cloud SQL Proxy
```bash
# On macOS using Homebrew
brew install cloud-sql-proxy

# Or download from: https://github.com/GoogleCloudSQL/cloud-sql-proxy/releases
```

### 2. Start Cloud SQL Proxy
```bash
cloud-sql-proxy hack-team-aialchemists-2026:asia-south1:alchemists-postgre \
  --port=5432 \
  --credentials-file=/path/to/service-account-key.json
```

### 3. Update Connection String (Already configured)
```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/monitoring_db
spring.datasource.username=alchmists_user
spring.datasource.password=Alchmists_pass5
```

## Option 2: Using Public IP

### 1. Get the Public IP of your Cloud SQL instance
```bash
gcloud sql instances describe alchemists-postgre --project=hack-team-aialchemists-2026
```

Look for `ipAddresses` field with type `PRIMARY`.

### 2. Update connection string
```properties
spring.datasource.url=jdbc:postgresql://YOUR_PUBLIC_IP:5432/monitoring_db
spring.datasource.username=alchmists_user
spring.datasource.password=Alchmists_pass5
```

### 3. Ensure Cloud SQL instance allows connections
- Go to Cloud SQL > alchemists-postgre > Connections
- Add your machine's public IP to the authorized networks

## Option 3: Using Unix Socket (If on same machine)

```properties
spring.datasource.url=jdbc:postgresql://localhost/monitoring_db
  ?socketFactoryArg=/cloudsql/hack-team-aialchemists-2026:asia-south1:alchemists-postgre
  &socketFactory=com.google.cloud.sql.postgres.SocketFactory
spring.datasource.username=alchmists_user
spring.datasource.password=Alchmists_pass5
```

Add dependency:
```xml
<dependency>
    <groupId>com.google.cloud.sql</groupId>
    <artifactId>cloud-sql-connector-postgres-socket-factory</artifactId>
    <version>1.14.0</version>
</dependency>
```

## Testing the Connection

After setting up the proxy or configuring the IP:

```bash
# Test connection with psql
psql -h localhost -U alchmists_user -d monitoring_db

# Or use curl to test the backend
curl http://localhost:8080/api/incidents
```

## Backend Startup

Once the connection is configured:

```bash
cd backend
mvn clean spring-boot:run
```

The backend will automatically:
- Create tables (INCIDENT_DETAILS, INCIDENT_RCA, etc.)
- Start the log monitoring scheduler
- Be ready to accept API requests

## Troubleshooting

### Connection Refused
- Verify Cloud SQL Proxy is running
- Check public IP is whitelisted
- Ensure database name is correct

### Authentication Failed
- Double-check username and password
- Verify user exists in the Cloud SQL instance

### Timeout
- Check network/firewall rules
- Verify Cloud SQL instance is running
- Check if proxy needs credentials file

## Current Configuration

File: `backend/src/main/resources/application.properties`

```properties
# Database Configuration (PostgreSQL)
spring.datasource.url=jdbc:postgresql://localhost:5432/monitoring_db
spring.datasource.username=alchmists_user
spring.datasource.password=Alchmists_pass5
spring.datasource.driver-class-name=org.postgresql.Driver

# JPA/Hibernate Configuration
spring.jpa.database-platform=org.hibernate.dialect.PostgreSQLDialect
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=false
```
