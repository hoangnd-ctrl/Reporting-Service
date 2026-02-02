package com.hoangnd.reportingservice.model.enums;

public enum FeedbackActionType {
    ASSIGN_REVIEWER,        // giao staff review feedback
    REVIEW_CONTENT,         // staff review nội dung
    REQUEST_REVISION,       // yêu cầu seller sửa
    APPROVE_FOR_PUBLISH,    // ok để publish
    REJECT_SUBMISSION,      // reject listing submission
    AI_GENERATED,
    SELLER_RESUBMITTED,
    CREATED,
    STATUS_CHANGED,
    AUTO_RESOLVED
}
