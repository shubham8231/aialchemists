package com.monitoring.repository;

import com.monitoring.entity.ApplicationDependency;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ApplicationDependencyRepository extends JpaRepository<ApplicationDependency, UUID> {

    List<ApplicationDependency> findByApplicationName(String applicationName);
}
