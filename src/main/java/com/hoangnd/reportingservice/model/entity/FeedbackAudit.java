package com.hoangnd.reportingservice.model.entity;

import com.hoangnd.reportingservice.model.enums.FeedbackActionType;
import com.hoangnd.reportingservice.model.enums.FeedbackStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "feedback_audit")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FeedbackAudit {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "feedback_audit_id")
    private UUID feedbackAuditId;

    // Bidirectional relationship with Feedback
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "feedback_id", nullable = false)
    private Feedback feedback;

    @Column(name = "admin_user_id")
    private UUID adminUserId;

    @Enumerated(EnumType.STRING)
    @Column(name = "action_type", nullable = false, length = 50)
    private FeedbackActionType feedbackActionType;

    @Enumerated(EnumType.STRING)
    @Column(name = "previous_state", nullable = false, length = 50)
    private FeedbackStatus previousState;

    @Enumerated(EnumType.STRING)
    @Column(name = "new_state", nullable = false, length = 50)
    private FeedbackStatus newState;

    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    @Builder.Default
    @Column(name = "is_automated", nullable = false)
    private boolean isAutomated = false;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @PrePersist
    void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }
}
