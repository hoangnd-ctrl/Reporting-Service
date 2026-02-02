package com.hoangnd.reportingservice.model.dto.request;

import com.hoangnd.reportingservice.model.enums.EvidenceType;
import com.hoangnd.reportingservice.model.enums.PriorityLevel;
import com.hoangnd.reportingservice.model.enums.ReportType;
import com.hoangnd.reportingservice.model.enums.ReportedEntityType;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateReportRequest {

    @NotNull(message = "Reporter user ID is required")
    private UUID reporterUserId;

    private UUID reportedUserId;

    @NotNull(message = "Reported entity type is required")
    private ReportedEntityType reportedEntityType;

    @NotNull(message = "Reported entity ID is required")
    private Long reportedEntityId;

    @NotNull(message = "Report type is required")
    private ReportType reportType;

    @NotNull(message = "Priority level is required")
    private PriorityLevel priorityLevel;

    @NotBlank(message = "Title is required")
    @Size(max = 500, message = "Title must be less than 500 characters")
    private String title;

    @NotBlank(message = "Description is required")
    private String description;

    private BigDecimal aiSeverityScore;

    @Valid
    private List<EvidenceRequest> evidences;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class EvidenceRequest {

        @NotNull(message = "Evidence type is required")
        private EvidenceType evidenceType;

        @NotBlank(message = "File URL is required")
        private String fileUrl;

        @NotNull(message = "File size is required")
        private Long fileSize;

        @NotBlank(message = "MIME type is required")
        private String mimeType;

        private String description;

        private String metadata;
    }
}
