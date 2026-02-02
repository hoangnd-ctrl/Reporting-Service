package com.hoangnd.reportingservice.controller;
import com.hoangnd.reportingservice.exception.ResourceNotFoundException;
import com.hoangnd.reportingservice.model.dto.request.CreateReportRequest;
import com.hoangnd.reportingservice.model.dto.response.ReportResponse;
import com.hoangnd.reportingservice.model.enums.ReportStatus;
import com.hoangnd.reportingservice.model.enums.ReportedEntityType;
import com.hoangnd.reportingservice.service.ReportService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/reports")
@RequiredArgsConstructor
public class ReportController {

    private final ReportService reportService;

    /**
     * Create new report
     * POST /api/v1/reports
     */
    @PostMapping
    public ResponseEntity<ReportResponse> createReport(@Valid @RequestBody CreateReportRequest request) {
        ReportResponse response = reportService.createReport(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Get report by ID
     * GET /api/v1/reports/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<ReportResponse> getReportById(@PathVariable UUID id) throws ResourceNotFoundException {
        ReportResponse response = reportService.getReportById(id);
        return ResponseEntity.ok(response);
    }

    /**
     * Get reports by reporter
     * GET /api/v1/reports/reporter/{reporterId}
     */
    @GetMapping("/reporter/{reporterId}")
    public ResponseEntity<List<ReportResponse>> getReportsByReporter(@PathVariable UUID reporterId) {
        List<ReportResponse> responses = reportService.getReportsByReporter(reporterId);
        return ResponseEntity.ok(responses);
    }

    /**
     * Get reports against a user
     * GET /api/v1/reports/reported-user/{userId}
     */
    @GetMapping("/reported-user/{userId}")
    public ResponseEntity<List<ReportResponse>> getReportsAgainstUser(@PathVariable UUID userId) {
        List<ReportResponse> responses = reportService.getReportsAgainstUser(userId);
        return ResponseEntity.ok(responses);
    }

    /**
     * Get reports by status
     * GET /api/v1/reports/status/{status}
     */
    @GetMapping("/status/{status}")
    public ResponseEntity<List<ReportResponse>> getReportsByStatus(@PathVariable ReportStatus status) {
        List<ReportResponse> responses = reportService.getReportsByStatus(status);
        return ResponseEntity.ok(responses);
    }

    /**
     * Get reports for a specific entity
     * GET /api/v1/reports/entity/{entityType}/{entityId}
     */
    @GetMapping("/entity/{entityType}/{entityId}")
    public ResponseEntity<List<ReportResponse>> getReportsByEntity(
            @PathVariable ReportedEntityType entityType,
            @PathVariable Long entityId) {
        List<ReportResponse> responses = reportService.getReportsByEntity(entityType, entityId);
        return ResponseEntity.ok(responses);
    }

    /**
     * Get reports assigned to admin
     * GET /api/v1/reports/assigned/{adminId}
     */
    @GetMapping("/assigned/{adminId}")
    public ResponseEntity<List<ReportResponse>> getReportsByAssignedAdmin(@PathVariable UUID adminId) {
        List<ReportResponse> responses = reportService.getReportsByAssignedAdmin(adminId);
        return ResponseEntity.ok(responses);
    }

    /**
     * Get unassigned reports
     * GET /api/v1/reports/unassigned
     */
    @GetMapping("/unassigned")
    public ResponseEntity<List<ReportResponse>> getUnassignedReports() {
        List<ReportResponse> responses = reportService.getUnassignedReports();
        return ResponseEntity.ok(responses);
    }

    /**
     * Get reports needing attention
     * GET /api/v1/reports/needs-attention
     */
    @GetMapping("/needs-attention")
    public ResponseEntity<List<ReportResponse>> getReportsNeedingAttention() {
        List<ReportResponse> responses = reportService.getReportsNeedingAttention();
        return ResponseEntity.ok(responses);
    }

    /**
     * Assign report to admin
     * PUT /api/v1/reports/{id}/assign
     */
    @PutMapping("/{id}/assign")
    public ResponseEntity<ReportResponse> assignReport(
            @PathVariable UUID id,
            @RequestParam UUID adminId,
            @RequestParam(required = false) String notes) throws ResourceNotFoundException {
        ReportResponse response = reportService.assignReport(id, adminId, notes);
        return ResponseEntity.ok(response);
    }

    /**
     * Update report status
     * PUT /api/v1/reports/{id}/status
     */
    @PutMapping("/{id}/status")
    public ResponseEntity<ReportResponse> updateStatus(
            @PathVariable UUID id,
            @RequestParam ReportStatus status,
            @RequestParam UUID adminId,
            @RequestParam(required = false) String notes) throws ResourceNotFoundException {
        ReportResponse response = reportService.updateReportStatus(id, status, adminId, notes);
        return ResponseEntity.ok(response);
    }

    /**
     * Resolve report
     * PUT /api/v1/reports/{id}/resolve
     */
    @PutMapping("/{id}/resolve")
    public ResponseEntity<ReportResponse> resolveReport(
            @PathVariable UUID id,
            @RequestParam UUID adminId,
            @RequestParam String resolutionNotes) throws ResourceNotFoundException {
        ReportResponse response = reportService.resolveReport(id, adminId, resolutionNotes);
        return ResponseEntity.ok(response);
    }

    /**
     * Reject report
     * PUT /api/v1/reports/{id}/reject
     */
    @PutMapping("/{id}/reject")
    public ResponseEntity<ReportResponse> rejectReport(
            @PathVariable UUID id,
            @RequestParam UUID adminId,
            @RequestParam String rejectionReason) throws ResourceNotFoundException {
        ReportResponse response = reportService.rejectReport(id, adminId, rejectionReason);
        return ResponseEntity.ok(response);
    }

    /**
     * Verify evidence
     * PUT /api/v1/reports/{reportId}/evidence/{evidenceId}/verify
     */
    @PutMapping("/{reportId}/evidence/{evidenceId}/verify")
    public ResponseEntity<ReportResponse> verifyEvidence(
            @PathVariable UUID reportId,
            @PathVariable UUID evidenceId,
            @RequestParam(required = false) String verificationNotes) throws ResourceNotFoundException {
        ReportResponse response = reportService.verifyEvidence(reportId, evidenceId, verificationNotes);
        return ResponseEntity.ok(response);
    }

    /**
     * Delete report
     * DELETE /api/v1/reports/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteReport(@PathVariable UUID id) throws ResourceNotFoundException {
        reportService.deleteReport(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Get report statistics
     * GET /api/v1/reports/statistics
     */
    @GetMapping("/statistics")
    public ResponseEntity<ReportService.ReportStatistics> getStatistics() {
        ReportService.ReportStatistics stats = reportService.getStatistics();
        return ResponseEntity.ok(stats);
    }
}
