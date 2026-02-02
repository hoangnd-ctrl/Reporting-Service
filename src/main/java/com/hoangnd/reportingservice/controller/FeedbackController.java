package com.hoangnd.reportingservice.controller;
import com.hoangnd.reportingservice.exception.ResourceNotFoundException;
import com.hoangnd.reportingservice.model.dto.request.CreateFeedbackRequest;
import com.hoangnd.reportingservice.model.dto.response.FeedbackResponse;
import com.hoangnd.reportingservice.model.enums.FeedbackStatus;
import com.hoangnd.reportingservice.service.FeedbackService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/feedback")
@RequiredArgsConstructor
public class FeedbackController {

    private final FeedbackService feedbackService;

    /**
     * Create new feedback
     * POST /api/v1/feedback
     */
    @PostMapping
    public ResponseEntity<FeedbackResponse> createFeedback(@Valid @RequestBody CreateFeedbackRequest request) {
        FeedbackResponse response = feedbackService.createFeedback(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Get feedback by ID
     * GET /api/v1/feedback/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<FeedbackResponse> getFeedbackById(@PathVariable UUID id) throws ResourceNotFoundException {
        FeedbackResponse response = feedbackService.getFeedbackById(id);
        return ResponseEntity.ok(response);
    }

    /**
     * Get all feedback for a listing
     * GET /api/v1/feedback/listing/{listingId}
     */
    @GetMapping("/listing/{listingId}")
    public ResponseEntity<List<FeedbackResponse>> getFeedbackByListing(@PathVariable UUID listingId) {
        List<FeedbackResponse> responses = feedbackService.getFeedbackByListing(listingId);
        return ResponseEntity.ok(responses);
    }

    /**
     * Get all feedback for a seller
     * GET /api/v1/feedback/seller/{sellerId}
     */
    @GetMapping("/seller/{sellerId}")
    public ResponseEntity<List<FeedbackResponse>> getFeedbackBySeller(@PathVariable UUID sellerId) {
        List<FeedbackResponse> responses = feedbackService.getFeedbackBySeller(sellerId);
        return ResponseEntity.ok(responses);
    }

    /**
     * Get feedback by status
     * GET /api/v1/feedback/status/{status}
     */
    @GetMapping("/status/{status}")
    public ResponseEntity<List<FeedbackResponse>> getFeedbackByStatus(@PathVariable FeedbackStatus status) {
        List<FeedbackResponse> responses = feedbackService.getFeedbackByStatus(status);
        return ResponseEntity.ok(responses);
    }

    /**
     * Get pending reviews
     * GET /api/v1/feedback/pending-reviews
     */
    @GetMapping("/pending-reviews")
    public ResponseEntity<List<FeedbackResponse>> getPendingReviews() {
        List<FeedbackResponse> responses = feedbackService.getPendingReviews();
        return ResponseEntity.ok(responses);
    }

    /**
     * Approve feedback
     * PUT /api/v1/feedback/{id}/approve
     */
    @PutMapping("/{id}/approve")
    public ResponseEntity<FeedbackResponse> approveFeedback(
            @PathVariable UUID id,
            @RequestParam UUID staffId,
            @RequestParam(required = false) String notes) throws ResourceNotFoundException {
        FeedbackResponse response = feedbackService.approveFeedback(id, staffId, notes);
        return ResponseEntity.ok(response);
    }

    /**
     * Reject feedback
     * PUT /api/v1/feedback/{id}/reject
     */
    @PutMapping("/{id}/reject")
    public ResponseEntity<FeedbackResponse> rejectFeedback(
            @PathVariable UUID id,
            @RequestParam UUID staffId,
            @RequestParam(required = false) String notes) throws ResourceNotFoundException {
        FeedbackResponse response = feedbackService.rejectFeedback(id, staffId, notes);
        return ResponseEntity.ok(response);
    }

    /**
     * Update feedback status
     * PUT /api/v1/feedback/{id}/status
     */
    @PutMapping("/{id}/status")
    public ResponseEntity<FeedbackResponse> updateStatus(
            @PathVariable UUID id,
            @RequestParam FeedbackStatus status,
            @RequestParam(required = false) UUID staffId,
            @RequestParam(required = false) String notes) throws ResourceNotFoundException {
        FeedbackResponse response = feedbackService.updateFeedbackStatus(id, status, staffId, notes);
        return ResponseEntity.ok(response);
    }

    /**
     * Mark feedback item as fixed
     * PUT /api/v1/feedback/{feedbackId}/items/{itemId}/fixed
     */
    @PutMapping("/{feedbackId}/items/{itemId}/fixed")
    public ResponseEntity<FeedbackResponse> markItemAsFixed(
            @PathVariable UUID feedbackId,
            @PathVariable UUID itemId) throws ResourceNotFoundException {
        FeedbackResponse response = feedbackService.markItemAsFixed(feedbackId, itemId);
        return ResponseEntity.ok(response);
    }

    /**
     * Delete feedback
     * DELETE /api/v1/feedback/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFeedback(@PathVariable UUID id) throws ResourceNotFoundException {
        feedbackService.deleteFeedback(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Get feedback statistics
     * GET /api/v1/feedback/statistics
     */
    @GetMapping("/statistics")
    public ResponseEntity<FeedbackService.FeedbackStatistics> getStatistics() {
        FeedbackService.FeedbackStatistics stats = feedbackService.getStatistics();
        return ResponseEntity.ok(stats);
    }
}
