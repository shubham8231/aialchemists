package com.monitoring.repository;

import com.monitoring.entity.IncidentRca;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IncidentRcaRepository extends JpaRepository<IncidentRca, String> {
}
