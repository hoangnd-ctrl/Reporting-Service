package com.hoangnd.reportingservice.service;

import com.hoangnd.reportingservice.exception.ResourceNotFoundException;
import com.hoangnd.reportingservice.model.dto.request.CreateFeedbackRequest;
import com.hoangnd.reportingservice.model.dto.response.FeedbackResponse;
import com.hoangnd.reportingservice.model.entity.Feedback;
import com.hoangnd.reportingservice.model.entity.FeedbackAudit;
import com.hoangnd.reportingservice.model.entity.FeedbackItem;
import com.hoangnd.reportingservice.model.enums.FeedbackActionType;
import com.hoangnd.reportingservice.model.enums.FeedbackStatus;
import com.hoangnd.reportingservice.repository.FeedbackRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class FeedbackService {
    private final FeedbackRepository feedbackRepository;

    /**
     * Create new feedback
     */
    @Transactional
    public FeedbackResponse createFeedback(CreateFeedbackRequest request) {
        log.info("Creating feedback for listing: {}", request.getListingId());

        // Build feedback entity
        Feedback feedback = Feedback.builder()
                .listingId(request.getListingId())
                .sellerUserId(request.getSellerUserId())
                .checkType(request.getCheckType())
                .feedbackStatus(FeedbackStatus.PENDING)
                .aiConfidenceScore(request.getAiConfidenceScore())
                .previousFeedbackId(request.getPreviousFeedbackId())
                .build();

        // Add feedback items
        if (request.getFeedbackItems() != null) {
            request.getFeedbackItems().forEach(itemRequest -> {
                FeedbackItem item = FeedbackItem.builder()
                        .category(itemRequest.getCategory())
                        .severity(itemRequest.getSeverity())
                        .targetAttribute(itemRequest.getTargetAttribute())
                        .errorMessage(itemRequest.getErrorMessage())
                        .suggestion(itemRequest.getSuggestion())
                        .detectedBy(itemRequest.getDetectedBy())
                        .build();
                feedback.addFeedbackItem(item);
            });
        }

        // Create audit log for creation
        FeedbackAudit audit = FeedbackAudit.builder()
                .feedbackActionType(FeedbackActionType.CREATED)
                .previousState(null)
                .newState(FeedbackStatus.PENDING)
                .notes("Feedback created")
                .isAutomated(true)
                .build();
        feedback.addFeedbackAudit(audit);

        // Save feedback
        Feedback savedFeedback = feedbackRepository.save(feedback);
        log.info("Feedback created with ID: {}", savedFeedback.getFeedbackId());

        return mapToResponse(savedFeedback);
    }

    /**
     * Get feedback by ID
     */
    @Transactional(readOnly = true)
    public FeedbackResponse getFeedbackById(UUID feedbackId) throws ResourceNotFoundException {
        log.info("Retrieving feedback with ID: {}", feedbackId);

        Feedback feedback = feedbackRepository.findByIdWithItems(feedbackId)
                .orElseThrow(() -> new ResourceNotFoundException("Feedback not found with ID: " + feedbackId));

        return mapToResponse(feedback);
    }

    /**
     * Get all feedback for a listing
     */
    @Transactional(readOnly = true)
    public List<FeedbackResponse> getFeedbackByListing(UUID listingId) {
        log.info("Retrieving feedbacks by listing: {}", listingId);

        List<Feedback> feedbacks = feedbackRepository.findByListingId(listingId);
        return feedbacks.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    // Get all feedback for a seller
    @Transactional(readOnly = true)
    public List<FeedbackResponse> getFeedbackBySeller(UUID sellerUserId) {
        log.info("Retrieving feedbacks by seller: {}", sellerUserId);

        List<Feedback> feedbacks = feedbackRepository.findBySellerUserId(sellerUserId);
        return feedbacks.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    // Get feedback by status
    @Transactional(readOnly = true)
    public List<FeedbackResponse> getFeedbackByStatus(FeedbackStatus status) {
        log.info("Retrieving feedbacks by status: {}", status);

        List<Feedback> feedbacks = feedbackRepository.findByFeedbackStatus(status);
        return feedbacks.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    // Update feedback status
    @Transactional(readOnly = true)
    public FeedbackResponse updateFeedbackStatus(UUID feedbackId, FeedbackStatus newStatus, UUID staffId, String notes) throws ResourceNotFoundException {
        log.info("Updating feedback {} to status: {}", feedbackId, newStatus);

        Feedback feedback = feedbackRepository.findById(feedbackId)
                .orElseThrow(() -> new ResourceNotFoundException("Feedback not found with ID: " + feedbackId));

        FeedbackStatus previousStatus = feedback.getFeedbackStatus();

        //Update status
        feedback.setFeedbackStatus(newStatus);
        if(staffId != null) {
            feedback.setReviewedByStaffId(staffId);
        }

        // create audit log
        FeedbackAudit audit = FeedbackAudit.builder()
                .feedbackActionType(FeedbackActionType.STATUS_CHANGED)
                .previousState(previousStatus)
                .newState(newStatus)
                .adminUserId(staffId)
                .notes(notes)
                .isAutomated(false)
                .build();
        feedback.addFeedbackAudit(audit);

        Feedback updatedFeedback = feedbackRepository.save(feedback);
        log.info("Feedback {} status updated from {} to {}", feedbackId, previousStatus, newStatus);

        return mapToResponse(updatedFeedback);
    }

    //Approve feedback
    @Transactional
    public FeedbackResponse approveFeedback(UUID feedbackId, UUID staffId, String notes) throws ResourceNotFoundException {
        return updateFeedbackStatus(feedbackId, FeedbackStatus.APPROVED, staffId, notes);
    }

    //Reject feedback
    @Transactional
    public FeedbackResponse rejectFeedback(UUID feedbackId, UUID staffId, String notes) throws ResourceNotFoundException {
        return updateFeedbackStatus(feedbackId, FeedbackStatus.REJECTED, staffId, notes);
    }

    //Mark feedback item as fixed
    @Transactional
    public FeedbackResponse markItemAsFixed(UUID feedbackId, UUID itemId) throws ResourceNotFoundException {
        log.info("Marking item as fixed: {}", itemId);

        Feedback feedback = feedbackRepository.findByIdWithItems(feedbackId)
                .orElseThrow(() -> new ResourceNotFoundException("Feedback not found with ID: " + feedbackId));

        FeedbackItem item = feedback.getFeedbackItems().stream()
                .filter(i -> i.getFeedbackItemId().equals(itemId))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Item not found with ID: " + itemId));

        item.setFixed(true);

        boolean allFixed = feedback.getFeedbackItems().stream()
                .allMatch(FeedbackItem::isFixed);

        if (allFixed && feedback.getFeedbackStatus() == FeedbackStatus.PENDING) {
            feedback.setFeedbackStatus(FeedbackStatus.RESOLVED);

            // Create audit log
            FeedbackAudit audit = FeedbackAudit.builder()
                    .feedbackActionType(FeedbackActionType.AUTO_RESOLVED)
                    .previousState(FeedbackStatus.PENDING)
                    .newState(FeedbackStatus.RESOLVED)
                    .notes("All items fixed - auto resolved")
                    .isAutomated(true)
                    .build();
            feedback.addFeedbackAudit(audit);
        }

        Feedback updatedFeedback = feedbackRepository.save(feedback);
        log.info("Feedback item {} marked as fixed", itemId);

        return mapToResponse(updatedFeedback);
    }

    /**
     * Delete feedback
     */
    @Transactional
    public void deleteFeedback(UUID feedbackId) throws ResourceNotFoundException {
        log.info("Deleting feedback with ID: {}", feedbackId);

        if (!feedbackRepository.existsById(feedbackId)) {
            throw new ResourceNotFoundException("Feedback not found with ID: " + feedbackId);
        }

        feedbackRepository.deleteById(feedbackId);
        log.info("Feedback {} deleted successfully", feedbackId);
    }

    /**
     * Get pending feedback requiring review
     */
    @Transactional(readOnly = true)
    public List<FeedbackResponse> getPendingReviews() {
        log.info("Retrieving pending feedback reviews");

        List<Feedback> feedbacks = feedbackRepository.findByFeedbackStatusAndReviewedByStaffIdIsNull(FeedbackStatus.PENDING);
        return feedbacks.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    /**
     * Get feedback statistics
     */
    @Transactional(readOnly = true)
    public FeedbackStatistics getStatistics() {
        log.info("Retrieving feedback statistics");

        return FeedbackStatistics.builder()
                .totalFeedback(feedbackRepository.count())
                .pendingCount(feedbackRepository.countByFeedbackStatus(FeedbackStatus.PENDING))
                .approvedCount(feedbackRepository.countByFeedbackStatus(FeedbackStatus.APPROVED))
                .rejectedCount(feedbackRepository.countByFeedbackStatus(FeedbackStatus.REJECTED))
                .resolvedCount(feedbackRepository.countByFeedbackStatus(FeedbackStatus.RESOLVED))
                .build();
    }

    // Mapper method
    private FeedbackResponse mapToResponse(Feedback feedback) {
        List<FeedbackResponse.FeedbackItemResponse> items = null;

        if (feedback.getFeedbackItems() != null) {
            items = feedback.getFeedbackItems().stream()
                    .map(item -> FeedbackResponse.FeedbackItemResponse.builder()
                            .feedbackItemId(item.getFeedbackItemId())
                            .category(item.getCategory())
                            .severity(item.getSeverity())
                            .targetAttribute(item.getTargetAttribute())
                            .errorMessage(item.getErrorMessage())
                            .suggestion(item.getSuggestion())
                            .detectedBy(item.getDetectedBy())
                            .isFixed(item.isFixed())
                            .createdAt(item.getCreatedAt())
                            .build())
                    .collect(Collectors.toList());
        }
        return FeedbackResponse.builder()
                .feedbackId(feedback.getFeedbackId())
                .listingId(feedback.getListingId())
                .sellerUserId(feedback.getSellerUserId())
                .checkType(feedback.getCheckType())
                .feedbackStatus(feedback.getFeedbackStatus())
                .aiConfidenceScore(feedback.getAiConfidenceScore())
                .createdAt(feedback.getCreatedAt())
                .reviewedByStaffId(feedback.getReviewedByStaffId())
                .isResubmission(feedback.isResubmission())
                .previousFeedbackId(feedback.getPreviousFeedbackId())
                .feedbackItems(items)
                .build();
    }

    // Statistics DTO
    @lombok.Data
    @lombok.Builder
    public static class FeedbackStatistics {
        private long totalFeedback;
        private long pendingCount;
        private long approvedCount;
        private long rejectedCount;
        private long resolvedCount;
    }
}
