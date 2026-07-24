package com.monitoring.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "application_dependency")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApplicationDependency {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "dependency_id")
    private UUID dependencyId;

    @Column(name = "application_name", nullable = false)
    private String applicationName;

    @Column(name = "ecosystem", nullable = false)
    private String ecosystem;

    @Column(name = "package_name", nullable = false)
    private String packageName;

    @Column(name = "version", nullable = false)
    private String version;

    @Column(name = "dependency_scope")
    private String dependencyScope;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
}
