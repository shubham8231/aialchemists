package com.monitoring.repository;

import com.monitoring.entity.IncidentAuditLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface IncidentAuditLogRepository extends JpaRepository<IncidentAuditLog, Long> {
    List<IncidentAuditLog> findByIncidentNoOrderByCreatedAtDesc(String incidentNo);

    List<IncidentAuditLog> findByIncidentNoAndProcessTypeOrderByCreatedAtDesc(String incidentNo, String processType);

    List<IncidentAuditLog> findByIncidentNoAndStatusOrderByCreatedAtDesc(String incidentNo, String status);
}
