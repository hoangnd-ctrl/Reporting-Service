package com.hoangnd.reportingservice.model.entity;

import com.hoangnd.reportingservice.model.enums.CheckType;
import com.hoangnd.reportingservice.model.enums.FeedbackStatus;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "feedback")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Feedback {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "feedback_id")
    private UUID feedbackId;

    @Column(name = "listing_id", nullable = false)
    private UUID listingId;

    @Column(name = "seller_user_id", nullable = false)
    private UUID sellerUserId;

    @Enumerated(EnumType.STRING)
    @Column(name = "check_type", length = 50)
    private CheckType checkType;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 50)
    private FeedbackStatus feedbackStatus = FeedbackStatus.PENDING;

    @Column(name = "ai_confidence_score", precision = 5, scale = 4)
    private BigDecimal aiConfidenceScore;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "reviewed_by_staff_id")
    private UUID reviewedByStaffId;

    @Builder.Default
    @Column(name = "is_resubmission", nullable = false)
    private boolean isResubmission = false;

    @Column(name = "previous_feedback_id")
    private UUID previousFeedbackId;

    @Builder.Default
    @OneToMany(mappedBy = "feedback", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<FeedbackItem> feedbackItems = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "feedback", cascade = CascadeType.ALL)
    private List<FeedbackAudit> feedbackAudits = new ArrayList<>();

    @PrePersist
    void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
        isResubmission = previousFeedbackId != null;
    }

    // Helper methods for managing bidirectional relationships
    public void addFeedbackItem(FeedbackItem item) {
        feedbackItems.add(item);
        item.setFeedback(this);
    }

    public void removeFeedbackItem(FeedbackItem item) {
        feedbackItems.remove(item);
        item.setFeedback(null);
    }

    public void addFeedbackAudit(FeedbackAudit audit) {
        feedbackAudits.add(audit);
        audit.setFeedback(this);
    }

    public void removeFeedbackAudit(FeedbackAudit audit) {
        feedbackAudits.remove(audit);
        audit.setFeedback(null);
    }
}
