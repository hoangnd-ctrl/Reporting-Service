package com.hoangnd.reportingservice.model.entity;

import com.hoangnd.reportingservice.model.enums.Category;
import com.hoangnd.reportingservice.model.enums.DetectedBy;
import com.hoangnd.reportingservice.model.enums.Severity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "feedback_item")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FeedbackItem {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "feedback_item_id")
    private UUID feedbackItemId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "feedback_id", nullable = false)
    private Feedback feedback;

    @Enumerated(EnumType.STRING)
    @Column(name = "category", nullable = false, length = 50)
    private Category category;

    @Enumerated(EnumType.STRING)
    @Column(name = "severity", nullable = false, length = 50)
    private Severity severity;

    @Column(name = "target_attribute", nullable = false, length = 255)
    private String targetAttribute;

    @Column(name = "error_message", nullable = false, columnDefinition = "TEXT")
    private String errorMessage;

    @Column(name = "suggestion", columnDefinition = "TEXT")
    private String suggestion;

    @Enumerated(EnumType.STRING)
    @Column(name = "detected_by", nullable = false, length = 50)
    private DetectedBy detectedBy;

    @Builder.Default
    @Column(name = "is_fixed", nullable = false)
    private boolean isFixed = false;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }
}
