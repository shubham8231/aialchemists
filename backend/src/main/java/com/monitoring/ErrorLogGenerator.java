package com.monitoring;

import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

@Component
public class ErrorLogGenerator {
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");
    private static final String[] SERVICES = {"auth-service", "payment-service", "user-service", "database-service", "ldap-service"};
    private final Random random = new Random();

    public List<String> generateNullPointerLogs() {
        List<String> logs = new ArrayList<>();
        String[] stackTraces = {
            "at com.example.service.UserService.getUserById(UserService.java:45)",
            "at com.example.service.OrderService.processOrder(OrderService.java:78)",
            "at com.example.controller.PaymentController.charge(PaymentController.java:102)"
        };

        for (int i = 0; i < 3; i++) {
            logs.add(String.format("%s ERROR [auth-service] traceId=%s - NullPointerException occurred in payment processing\n" +
                    "java.lang.NullPointerException: Cannot invoke method getBalance() on null object\n" +
                    "%s\n" +
                    "%s",
                    FORMATTER.format(LocalDateTime.now().minusHours(random.nextInt(24))),
                    UUID.randomUUID(),
                    stackTraces[random.nextInt(stackTraces.length)],
                    "Caused by: User object returned null from database query"));
        }
        return logs;
    }

    public List<String> generateLdapExceptionLogs() {
        List<String> logs = new ArrayList<>();
        String[] errors = {
            "javax.naming.CommunicationException: ldap.company.com:389",
            "javax.naming.AuthenticationException: [LDAP: error code 49 - 80090308: LdapErr: DSID-0C0903E9",
            "javax.naming.NameNotFoundException: [LDAP: error code 32 - No Such Object]"
        };

        for (int i = 0; i < 3; i++) {
            logs.add(String.format("%s WARN [auth-service] service=ldap-service traceId=%s - LDAP authentication failed\n" +
                    "%s\n" +
                    "Failed to bind user: CN=admin,DC=company,DC=com\n" +
                    "Retrying connection in 5 seconds...",
                    FORMATTER.format(LocalDateTime.now().minusHours(random.nextInt(24))),
                    UUID.randomUUID(),
                    errors[random.nextInt(errors.length)]));
        }
        return logs;
    }

    public List<String> generateDbTimeoutLogs() {
        List<String> logs = new ArrayList<>();
        String[] queries = {
            "SELECT * FROM users WHERE status='active' AND created_date > now() - interval '30 days'",
            "INSERT INTO transaction_logs (user_id, amount, status) VALUES (?, ?, ?)",
            "UPDATE orders SET status='shipped' WHERE order_date < now() - interval '2 days'"
        };

        for (int i = 0; i < 3; i++) {
            logs.add(String.format("%s ERROR [database-service] service=postgres-db traceId=%s - Database query timeout\n" +
                    "Query execution exceeded 30000ms timeout threshold\n" +
                    "Query: %s\n" +
                    "Connection ID: conn_%d\n" +
                    "org.postgresql.util.PSQLException: ERROR: canceling statement due to user request",
                    FORMATTER.format(LocalDateTime.now().minusHours(random.nextInt(24))),
                    UUID.randomUUID(),
                    queries[random.nextInt(queries.length)],
                    random.nextInt(10000)));
        }
        return logs;
    }

    public List<String> generateDbPoolExhaustedLogs() {
        List<String> logs = new ArrayList<>();

        for (int i = 0; i < 3; i++) {
            logs.add(String.format("%s ERROR [database-service] service=postgres-db traceId=%s - Connection pool exhausted\n" +
                    "Cannot obtain JDBC Connection; nested exception is:\n" +
                    "java.sql.SQLException: Cannot get a connection, pool error Timeout waiting for idle object\n" +
                    "Current pool state: active=20, idle=0, max=20\n" +
                    "Pending requests: 15\n" +
                    "Waiting time exceeded 60000ms",
                    FORMATTER.format(LocalDateTime.now().minusHours(random.nextInt(24))),
                    UUID.randomUUID()));
        }
        return logs;
    }

    public List<String> generateTransactionDeclinedLogs() {
        List<String> logs = new ArrayList<>();
        String[] reasons = {
            "Card declined - Insufficient funds",
            "Card declined - Fraud detection triggered",
            "Card declined - CVV mismatch",
            "Card declined - Expired card",
            "Card declined - Daily limit exceeded"
        };

        for (int i = 0; i < 3; i++) {
            logs.add(String.format("%s WARN [payment-service] service=payment-gateway traceId=%s - Transaction declined\n" +
                    "Reason: %s\n" +
                    "Card Last 4: **** **** **** 4242\n" +
                    "Amount: $%d.%d\n" +
                    "MerchantCode: error_code_%d\n" +
                    "AuthorizationToken: declined",
                    FORMATTER.format(LocalDateTime.now().minusHours(random.nextInt(24))),
                    UUID.randomUUID(),
                    reasons[random.nextInt(reasons.length)],
                    random.nextInt(1000),
                    random.nextInt(100),
                    random.nextInt(100)));
        }
        return logs;
    }

    public List<String> generatePostgresqlErrorLogs() {
        List<String> logs = new ArrayList<>();
        String[] pgErrors = {
            "ERROR: duplicate key value violates unique constraint \"users_email_key\"",
            "ERROR: relation \"transactions\" does not exist at character 15",
            "ERROR: permission denied for schema public",
            "ERROR: invalid input syntax for type integer: \"abc123\"",
            "ERROR: deadlock detected"
        };

        for (int i = 0; i < 3; i++) {
            logs.add(String.format("%s ERROR [database-service] service=postgres traceId=%s - PostgreSQL Error\n" +
                    "%s\n" +
                    "Location: table.c:1234, ExecInsert()\n" +
                    "Database: production_db\n" +
                    "User: app_user\n" +
                    "Backend PID: %d",
                    FORMATTER.format(LocalDateTime.now().minusHours(random.nextInt(24))),
                    UUID.randomUUID(),
                    pgErrors[random.nextInt(pgErrors.length)],
                    random.nextInt(50000)));
        }
        return logs;
    }

    public List<String> getAllErrorLogs() {
        List<String> allLogs = new ArrayList<>();
        allLogs.addAll(generateNullPointerLogs());
        allLogs.addAll(generateLdapExceptionLogs());
        allLogs.addAll(generateDbTimeoutLogs());
        allLogs.addAll(generateDbPoolExhaustedLogs());
        allLogs.addAll(generateTransactionDeclinedLogs());
        allLogs.addAll(generatePostgresqlErrorLogs());
        return allLogs;
    }
}
