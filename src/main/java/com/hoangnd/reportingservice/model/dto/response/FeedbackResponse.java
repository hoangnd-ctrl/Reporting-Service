package com.hoangnd.reportingservice.model.dto.response;

import com.hoangnd.reportingservice.model.enums.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FeedbackResponse {

    private UUID feedbackId;
    private UUID listingId;
    private UUID sellerUserId;
    private CheckType checkType;
    private FeedbackStatus feedbackStatus;
    private BigDecimal aiConfidenceScore;
    private LocalDateTime createdAt;
    private UUID reviewedByStaffId;
    private boolean isResubmission;
    private UUID previousFeedbackId;
    private List<FeedbackItemResponse> feedbackItems;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FeedbackItemResponse {
        private UUID feedbackItemId;
        private Category category;
        private Severity severity;
        private String targetAttribute;
        private String errorMessage;
        private String suggestion;
        private DetectedBy detectedBy;
        private boolean isFixed;
        private LocalDateTime createdAt;
    }
}
