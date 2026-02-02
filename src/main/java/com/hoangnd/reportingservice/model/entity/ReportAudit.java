package com.hoangnd.reportingservice.model.entity;

import com.hoangnd.reportingservice.model.enums.ReportActionType;
import com.hoangnd.reportingservice.model.enums.ReportStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "report_audits")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReportAudit {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "report_audit_id")
    private UUID reportAuditId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "report_id", nullable = false)
    private Report report;

    @Column(name = "admin_user_id")
    private UUID adminUserId;

    @Enumerated(EnumType.STRING)
    @Column(name = "action_type", nullable = false, length = 50)
    private ReportActionType actionType;

    @Enumerated(EnumType.STRING)
    @Column(name = "previous_state", length = 50)
    private ReportStatus previousState;

    @Enumerated(EnumType.STRING)
    @Column(name = "new_state", length = 50)
    private ReportStatus newState;

    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    @Builder.Default
    @Column(name = "is_automated", nullable = false)
    private boolean isAutomated = false;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }
}
