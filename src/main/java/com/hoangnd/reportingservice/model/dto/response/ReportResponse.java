package com.hoangnd.reportingservice.model.dto.response;

import com.hoangnd.reportingservice.model.enums.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReportResponse {
    private UUID reportId;
    private UUID reporterUserId;
    private UUID reportedUserId;
    private ReportedEntityType reportedEntityType;
    private Long reportedEntityId;
    private ReportType reportType;
    private PriorityLevel priorityLevel;
    private ReportStatus status;
    private String title;
    private String description;
    private UUID assignedAdminId;
    private String resolutionNotes;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime resolvedAt;
    private BigDecimal aiSeverityScore;
    private boolean aiVerified;
    private List<EvidenceResponse> evidences;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class EvidenceResponse {
        private UUID evidenceId;
        private EvidenceType evidenceType;
        private String fileUrl;
        private Long fileSize;
        private String mimeType;
        private String description;
        private String metadata;
        private LocalDateTime uploadedAt;
        private boolean isVerified;
        private String verificationNotes;
    }
}
