package com.hoangnd.reportingservice.model.dto.request;

import com.hoangnd.reportingservice.model.enums.Category;
import com.hoangnd.reportingservice.model.enums.DetectedBy;
import com.hoangnd.reportingservice.model.enums.Severity;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FeedbackItemRequest {

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
