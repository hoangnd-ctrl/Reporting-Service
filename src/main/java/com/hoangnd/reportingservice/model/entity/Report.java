package com.hoangnd.reportingservice.model.entity;

import com.hoangnd.reportingservice.model.enums.*;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "report")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Report {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "report_id")
    private UUID reportId;

    @Column(name = "reporter_user_id", nullable = false)
    private UUID reporterUserId;

    @Column(name = "reported_user_id")
    private UUID reportedUserId;

    @Enumerated(EnumType.STRING)
    @Column(name = "reported_entity_type", nullable = false, length = 50)
    private ReportedEntityType reportedEntityType;

    @Column(name = "reported_entity_id", nullable = false)
    private Long reportedEntityId;

    @Enumerated(EnumType.STRING)
    @Column(name = "report_type", nullable = false, length = 50)
    private ReportType reportType;

    @Enumerated(EnumType.STRING)
    @Column(name = "priority_level", nullable = false, length = 50)
    private PriorityLevel priorityLevel;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 50)
    private ReportStatus status = ReportStatus.PENDING;

    @Column(name = "title", nullable = false, length = 500)
    private String title;

    @Column(name = "description", nullable = false, columnDefinition = "TEXT")
    private String description;

    @Column(name = "assigned_admin_id")
    private UUID assignedAdminId;

    @Column(name = "resolution_notes")
    private String resolutionNotes;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "resolved_at")
    private LocalDateTime resolvedAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "ai_severity_score", precision = 5, scale = 4)
    private BigDecimal aiSeverityScore;

    @Builder.Default
    @Column(name = "ai_verified", nullable = false)
    private boolean aiVerified = false;

    @Builder.Default
    @OneToMany(mappedBy = "report", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ReportEvidence> evidences = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "report", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ReportAudit> audits = new ArrayList<>();

    @PrePersist
    void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }

    @PreUpdate
    void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    // Helper methods for managing bidirectional relationships
    public void addEvidence(ReportEvidence evidence) {
        evidences.add(evidence);
        evidence.setReport(this);
    }

    public void removeEvidence(ReportEvidence evidence) {
        evidences.remove(evidence);
        evidence.setReport(null);
    }

    public void addAudit(ReportAudit audit) {
        audits.add(audit);
        audit.setReport(this);
    }

    public void removeAudit(ReportAudit audit) {
        audits.remove(audit);
        audit.setReport(null);
    }
}
