package com.hoangnd.reportingservice.model.dto.request;

import com.hoangnd.reportingservice.model.enums.Category;
import com.hoangnd.reportingservice.model.enums.CheckType;
import com.hoangnd.reportingservice.model.enums.DetectedBy;
import com.hoangnd.reportingservice.model.enums.Severity;
import jakarta.validation.Valid;
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
public class CreateFeedbackRequest {
    @NotNull(message = "Listing ID is required")
    private UUID listingId;

    @NotNull(message = "Seller user ID is required")
    private UUID sellerUserId;

    @NotNull(message = "Check type is required")
    private CheckType checkType;

    private BigDecimal aiConfidenceScore;

    private UUID previousFeedbackId;

    @Valid
    @Size(min = 1, message = "At least one feedback item is required")
    private List<FeedbackItemRequest> feedbackItems;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FeedbackItemRequest {

        @NotNull(message = "Category is required")
        private Category category;

        @NotNull(message = "Severity is required")
        private Severity severity;

        @NotNull(message = "Target attribute is required")
        private String targetAttribute;

        @NotNull(message = "Error message is required")
        private String errorMessage;

        private String suggestion;

        @NotNull(message = "Detected by is required")
        private DetectedBy detectedBy;
    }
}
