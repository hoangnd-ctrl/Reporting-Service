package com.hoangnd.reportingservice.model.entity;

import com.hoangnd.reportingservice.model.enums.EvidenceType;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "report_evidence")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReportEvidence {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "evidence_id")
    private UUID evidenceId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "report_id", nullable = false)
    private Report report;

    @Enumerated(EnumType.STRING)
    @Column(name = "evidence_type", nullable = false, length = 50)
    private EvidenceType evidenceType;

    @Column(name = "file_url", nullable = false, length = 2048)
    private String fileUrl;

    @Column(name = "file_size", nullable = false)
    private Long fileSize;

    @Column(name = "mime_type", nullable = false, length = 255)
    private String mimeType;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "metadata", columnDefinition = "TEXT")
    private String metadata;

    @Column(name = "uploaded_at", nullable = false, updatable = false)
    private LocalDateTime uploadedAt;

    @Builder.Default
    @Column(name = "is_verified", nullable = false)
    private boolean isVerified = false;

    @Column(name = "verification_notes", columnDefinition = "TEXT")
    private String verificationNotes;

    @PrePersist
    void onUpload() {
        if (uploadedAt == null) {
            uploadedAt = LocalDateTime.now();
        }
    }
}
