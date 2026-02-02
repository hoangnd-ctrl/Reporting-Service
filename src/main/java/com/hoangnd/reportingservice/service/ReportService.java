package com.hoangnd.reportingservice.service;

import com.hoangnd.reportingservice.exception.ResourceNotFoundException;
import com.hoangnd.reportingservice.model.dto.request.CreateReportRequest;
import com.hoangnd.reportingservice.model.dto.response.ReportResponse;
import com.hoangnd.reportingservice.model.entity.Report;
import com.hoangnd.reportingservice.model.entity.ReportAudit;
import com.hoangnd.reportingservice.model.entity.ReportEvidence;
import com.hoangnd.reportingservice.model.enums.ReportActionType;
import com.hoangnd.reportingservice.model.enums.ReportStatus;
import com.hoangnd.reportingservice.model.enums.ReportedEntityType;
import com.hoangnd.reportingservice.repository.ReportRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReportService {
    private final ReportRepository reportRepository;

    /**
     * Create new report
     */
    @Transactional
    public ReportResponse createReport(CreateReportRequest request) {
        log.info("Creating report for entity: {} with ID: {}",
                request.getReportedEntityType(), request.getReportedEntityId());

        // Build report entity
        Report report = Report.builder()
                .reporterUserId(request.getReporterUserId())
                .reportedUserId(request.getReportedUserId())
                .reportedEntityType(request.getReportedEntityType())
                .reportedEntityId(request.getReportedEntityId())
                .reportType(request.getReportType())
                .priorityLevel(request.getPriorityLevel())
                .title(request.getTitle())
                .description(request.getDescription())
                .aiSeverityScore(request.getAiSeverityScore())
                .build();

        // Add evidence if provided
        if (request.getEvidences() != null) {
            request.getEvidences().forEach(evidenceRequest -> {
                ReportEvidence evidence = ReportEvidence.builder()
                        .evidenceType(evidenceRequest.getEvidenceType())
                        .fileUrl(evidenceRequest.getFileUrl())
                        .fileSize(evidenceRequest.getFileSize())
                        .mimeType(evidenceRequest.getMimeType())
                        .description(evidenceRequest.getDescription())
                        .metadata(evidenceRequest.getMetadata())
                        .build();
                report.addEvidence(evidence);
            });
        }

        // Create audit log for creation
        ReportAudit audit = ReportAudit.builder()
                .actionType(ReportActionType.CREATED)
                .previousState(null)
                .newState(ReportStatus.PENDING)
                .notes("Report created")
                .isAutomated(true)
                .build();
        report.addAudit(audit);

        // Save report
        Report savedReport = reportRepository.save(report);
        log.info("Report created with ID: {}", savedReport.getReportId());

        return mapToResponse(savedReport);
    }

    /**
     * Get report by ID
     */
    @Transactional(readOnly = true)
    public ReportResponse getReportById(UUID reportId) throws ResourceNotFoundException {
        log.info("Retrieving report with ID: {}", reportId);

        Report report = reportRepository.findByIdWithEvidences(reportId)
                .orElseThrow(() -> new ResourceNotFoundException("Report not found with ID: " + reportId));

        return mapToResponse(report);
    }

    /**
     * Get all reports by reporter
     */
    @Transactional(readOnly = true)
    public List<ReportResponse> getReportsByReporter(UUID reporterUserId) {
        log.info("Retrieving reports for reporter: {}", reporterUserId);

        List<Report> reports = reportRepository.findByReporterUserId(reporterUserId);
        return reports.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    /**
     * Get all reports against a user
     */
    @Transactional(readOnly = true)
    public List<ReportResponse> getReportsAgainstUser(UUID reportedUserId) {
        log.info("Retrieving reports against user: {}", reportedUserId);

        List<Report> reports = reportRepository.findByReportedUserId(reportedUserId);
        return reports.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    /**
     * Get reports by status
     */
    @Transactional(readOnly = true)
    public List<ReportResponse> getReportsByStatus(ReportStatus status) {
        log.info("Retrieving reports with status: {}", status);

        List<Report> reports = reportRepository.findByStatus(status);
        return reports.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    /**
     * Get reports for a specific entity
     */
    @Transactional(readOnly = true)
    public List<ReportResponse> getReportsByEntity(ReportedEntityType entityType, Long entityId) {
        log.info("Retrieving reports for entity type: {} with ID: {}", entityType, entityId);

        List<Report> reports = reportRepository.findByReportedEntityTypeAndReportedEntityId(entityType, entityId);
        return reports.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    /**
     * Get reports assigned to admin
     */
    @Transactional(readOnly = true)
    public List<ReportResponse> getReportsByAssignedAdmin(UUID adminId) {
        log.info("Retrieving reports assigned to admin: {}", adminId);

        List<Report> reports = reportRepository.findByAssignedAdminId(adminId);
        return reports.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    /**
     * Get unassigned reports
     */
    @Transactional(readOnly = true)
    public List<ReportResponse> getUnassignedReports() {
        log.info("Retrieving unassigned reports");

        List<Report> reports = reportRepository.findByAssignedAdminIdIsNull();
        return reports.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    /**
     * Get reports needing attention (high priority, unassigned)
     */
    @Transactional(readOnly = true)
    public List<ReportResponse> getReportsNeedingAttention() {
        log.info("Retrieving reports needing attention");

        List<Report> reports = reportRepository.findReportsNeedingAttention();
        return reports.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    /**
     * Assign report to admin
     */
    @Transactional
    public ReportResponse assignReport(UUID reportId, UUID adminId, String notes) throws ResourceNotFoundException {
        log.info("Assigning report {} to admin: {}", reportId, adminId);

        Report report = reportRepository.findById(reportId)
                .orElseThrow(() -> new ResourceNotFoundException("Report not found with ID: " + reportId));

        UUID previousAdmin = report.getAssignedAdminId();
        report.setAssignedAdminId(adminId);

        // Create audit log
        ReportAudit audit = ReportAudit.builder()
                .actionType(ReportActionType.ASSIGNED)
                .previousState(report.getStatus())
                .newState(report.getStatus())
                .adminUserId(adminId)
                .notes(notes != null ? notes : "Report assigned to admin")
                .isAutomated(false)
                .build();
        report.addAudit(audit);

        Report updatedReport = reportRepository.save(report);
        log.info("Report {} assigned to admin {}", reportId, adminId);

        return mapToResponse(updatedReport);
    }

    /**
     * Update report status
     */
    @Transactional
    public ReportResponse updateReportStatus(UUID reportId, ReportStatus newStatus, UUID adminId, String notes) throws ResourceNotFoundException {
        log.info("Updating report {} to status: {}", reportId, newStatus);

        Report report = reportRepository.findById(reportId)
                .orElseThrow(() -> new ResourceNotFoundException("Report not found with ID: " + reportId));

        ReportStatus previousStatus = report.getStatus();
        report.setStatus(newStatus);

        // Set resolved timestamp if resolving
        if (newStatus == ReportStatus.RESOLVED && report.getResolvedAt() == null) {
            report.setResolvedAt(LocalDateTime.now());
        }

        // Create audit log
        ReportAudit audit = ReportAudit.builder()
                .actionType(ReportActionType.STATUS_CHANGED)
                .previousState(previousStatus)
                .newState(newStatus)
                .adminUserId(adminId)
                .notes(notes)
                .isAutomated(false)
                .build();
        report.addAudit(audit);

        Report updatedReport = reportRepository.save(report);
        log.info("Report {} status updated from {} to {}", reportId, previousStatus, newStatus);

        return mapToResponse(updatedReport);
    }

    /**
     * Resolve report
     */
    @Transactional
    public ReportResponse resolveReport(UUID reportId, UUID adminId, String resolutionNotes) throws ResourceNotFoundException {
        log.info("Resolving report: {}", reportId);

        Report report = reportRepository.findById(reportId)
                .orElseThrow(() -> new ResourceNotFoundException("Report not found with ID: " + reportId));

        report.setStatus(ReportStatus.RESOLVED);
        report.setResolvedAt(LocalDateTime.now());
        report.setResolutionNotes(resolutionNotes);

        // Create audit log
        ReportAudit audit = ReportAudit.builder()
                .actionType(ReportActionType.RESOLVED)
                .previousState(report.getStatus())
                .newState(ReportStatus.RESOLVED)
                .adminUserId(adminId)
                .notes(resolutionNotes)
                .isAutomated(false)
                .build();
        report.addAudit(audit);

        Report resolvedReport = reportRepository.save(report);
        log.info("Report {} resolved successfully", reportId);

        return mapToResponse(resolvedReport);
    }

    /**
     * Reject report
     */
    @Transactional
    public ReportResponse rejectReport(UUID reportId, UUID adminId, String rejectionReason) throws ResourceNotFoundException {
        log.info("Rejecting report: {}", reportId);

        return updateReportStatus(reportId, ReportStatus.REJECTED, adminId, rejectionReason);
    }

    /**
     * Delete report
     */
    @Transactional
    public void deleteReport(UUID reportId) throws ResourceNotFoundException {
        log.info("Deleting report with ID: {}", reportId);

        if (!reportRepository.existsById(reportId)) {
            throw new ResourceNotFoundException("Report not found with ID: " + reportId);
        }

        reportRepository.deleteById(reportId);
        log.info("Report {} deleted successfully", reportId);
    }

    /**
     * Verify evidence
     */
    @Transactional
    public ReportResponse verifyEvidence(UUID reportId, UUID evidenceId, String verificationNotes) throws ResourceNotFoundException {
        log.info("Verifying evidence {} for report {}", evidenceId, reportId);

        Report report = reportRepository.findByIdWithEvidences(reportId)
                .orElseThrow(() -> new ResourceNotFoundException("Report not found with ID: " + reportId));

        ReportEvidence evidence = report.getEvidences().stream()
                .filter(e -> e.getEvidenceId().equals(evidenceId))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Evidence not found with ID: " + evidenceId));

        evidence.setVerified(true);
        evidence.setVerificationNotes(verificationNotes);

        Report updatedReport = reportRepository.save(report);
        log.info("Evidence {} verified for report {}", evidenceId, reportId);

        return mapToResponse(updatedReport);
    }

    /**
     * Get report statistics
     */
    @Transactional(readOnly = true)
    public ReportStatistics getStatistics() {
        log.info("Retrieving report statistics");

        return ReportStatistics.builder()
                .totalReports(reportRepository.count())
                .pendingCount(reportRepository.countByStatus(ReportStatus.PENDING))
                .underReviewCount(reportRepository.countByStatus(ReportStatus.UNDER_REVIEW))
                .resolvedCount(reportRepository.countByStatus(ReportStatus.RESOLVED))
                .rejectedCount(reportRepository.countByStatus(ReportStatus.REJECTED))
                .build();
    }

    // Mapper method
    private ReportResponse mapToResponse(Report report) {
        List<ReportResponse.EvidenceResponse> evidences = null;

        if (report.getEvidences() != null) {
            evidences = report.getEvidences().stream()
                    .map(evidence -> ReportResponse.EvidenceResponse.builder()
                            .evidenceId(evidence.getEvidenceId())
                            .evidenceType(evidence.getEvidenceType())
                            .fileUrl(evidence.getFileUrl())
                            .fileSize(evidence.getFileSize())
                            .mimeType(evidence.getMimeType())
                            .description(evidence.getDescription())
                            .metadata(evidence.getMetadata())
                            .uploadedAt(evidence.getUploadedAt())
                            .isVerified(evidence.isVerified())
                            .verificationNotes(evidence.getVerificationNotes())
                            .build())
                    .collect(Collectors.toList());
        }

        return ReportResponse.builder()
                .reportId(report.getReportId())
                .reporterUserId(report.getReporterUserId())
                .reportedUserId(report.getReportedUserId())
                .reportedEntityType(report.getReportedEntityType())
                .reportedEntityId(report.getReportedEntityId())
                .reportType(report.getReportType())
                .priorityLevel(report.getPriorityLevel())
                .status(report.getStatus())
                .title(report.getTitle())
                .description(report.getDescription())
                .assignedAdminId(report.getAssignedAdminId())
                .resolutionNotes(report.getResolutionNotes())
                .createdAt(report.getCreatedAt())
                .updatedAt(report.getUpdatedAt())
                .resolvedAt(report.getResolvedAt())
                .aiSeverityScore(report.getAiSeverityScore())
                .aiVerified(report.isAiVerified())
                .evidences(evidences)
                .build();
    }

    // Statistics DTO
    @lombok.Data
    @lombok.Builder
    public static class ReportStatistics {
        private long totalReports;
        private long pendingCount;
        private long underReviewCount;
        private long resolvedCount;
        private long rejectedCount;
    }
}
