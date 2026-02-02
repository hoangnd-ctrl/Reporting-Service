package com.hoangnd.reportingservice.repository;

import com.hoangnd.reportingservice.model.entity.Report;
import com.hoangnd.reportingservice.model.entity.ReportAudit;
import com.hoangnd.reportingservice.model.enums.PriorityLevel;
import com.hoangnd.reportingservice.model.enums.ReportStatus;
import com.hoangnd.reportingservice.model.enums.ReportType;
import com.hoangnd.reportingservice.model.enums.ReportedEntityType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ReportRepository extends JpaRepository<Report, UUID> {
    // Find by reporter
    List<Report> findByReporterUserId(UUID reporterUserId);

    // Find by reported user
    List<Report> findByReportedUserId(UUID reportedUserId);

    // Find by status
    List<Report> findByStatus(ReportStatus status);

    // Find by priority
    List<Report> findByPriorityLevel(PriorityLevel priorityLevel);

    // Find by type
    List<Report> findByReportType(ReportType reportType);

    // Find by entity
    List<Report> findByReportedEntityTypeAndReportedEntityId(
            ReportedEntityType entityType,
            Long entityId
    );

    // Find by assigned admin
    List<Report> findByAssignedAdminId(UUID adminId);

    // Find unassigned reports
    List<Report> findByAssignedAdminIdIsNull();

    // Find by status and priority
    List<Report> findByStatusAndPriorityLevel(ReportStatus status, PriorityLevel priority);

    // Find urgent pending reports
    List<Report> findByStatusAndPriorityLevelOrderByCreatedAtAsc(
            ReportStatus status,
            PriorityLevel priority
    );

    // Find AI verified reports
    List<Report> findByAiVerifiedTrue();

    // Find created between dates
    List<Report> findByCreatedAtBetween(LocalDateTime start, LocalDateTime end);

    // Find resolved between dates
    List<Report> findByResolvedAtBetween(LocalDateTime start, LocalDateTime end);

    // Count by status
    long countByStatus(ReportStatus status);

    // Count by type
    long countByReportType(ReportType reportType);

    // Count reports against a user
    long countByReportedUserId(UUID userId);

    // Check if entity has pending reports
    boolean existsByReportedEntityTypeAndReportedEntityIdAndStatus(
            ReportedEntityType entityType,
            Long entityId,
            ReportStatus status
    );

    // Custom query: Find overdue reports (pending > 7 days)
    @Query("SELECT r FROM Report r WHERE r.status = 'PENDING' " +
            "AND r.createdAt < :cutoffDate")
    List<Report> findOverdueReports(@Param("cutoffDate") LocalDateTime cutoffDate);

    // Find reports needing attention (high priority, unassigned)
    @Query("SELECT r FROM Report r WHERE r.priorityLevel IN ('HIGH', 'CRITICAL') " +
            "AND r.status = 'PENDING' " +
            "AND r.assignedAdminId IS NULL " +
            "ORDER BY r.createdAt ASC")
    List<Report> findReportsNeedingAttention();

    // Find with evidences (fetch join to avoid N+1)
    @Query("SELECT DISTINCT r FROM Report r LEFT JOIN FETCH r.evidences WHERE r.reportId = :id")
    Optional<Report> findByIdWithEvidences(@Param("id") UUID id);

    // Find with audits
    @Query("SELECT DISTINCT r FROM Report r LEFT JOIN FETCH r.audits WHERE r.reportId = :id")
    Optional<Report> findByIdWithAudits(@Param("id") UUID id);

    // Find with all relationships
    @Query("SELECT DISTINCT r FROM Report r " +
            "LEFT JOIN FETCH r.evidences " +
            "LEFT JOIN FETCH r.audits " +
            "WHERE r.reportId = :id")
    Optional<Report> findByIdWithAll(@Param("id") UUID id);

    // Statistics query: Count reports by status
    @Query("SELECT r.status, COUNT(r) FROM Report r GROUP BY r.status")
    List<Object[]> countReportsByStatus();

    // Statistics query: Count reports by type
    @Query("SELECT r.reportType, COUNT(r) FROM Report r GROUP BY r.reportType")
    List<Object[]> countReportsByType();
}
