package com.monitoring.repository;

import com.monitoring.entity.IncidentDetails;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface IncidentDetailsRepository extends JpaRepository<IncidentDetails, String> {
    Page<IncidentDetails> findByApplicationName(String applicationName, Pageable pageable);

    Page<IncidentDetails> findBySeverity(String severity, Pageable pageable);

    List<IncidentDetails> findBySeverity(String severity);

    Page<IncidentDetails> findByStatus(String status, Pageable pageable);

    Page<IncidentDetails> findByApplicationNameAndStatus(String applicationName, String status, Pageable pageable);

    List<IncidentDetails> findByErrorType(String errorType);

    Optional<IncidentDetails> findByIncidentNo(String incidentNo);

    Page<IncidentDetails> findAll(Pageable pageable);

    long countByStatus(String status);

    long countBySeverity(String severity);
}
