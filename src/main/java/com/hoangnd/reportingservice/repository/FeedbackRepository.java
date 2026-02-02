package com.hoangnd.reportingservice.repository;

import com.hoangnd.reportingservice.model.entity.Feedback;
import com.hoangnd.reportingservice.model.enums.FeedbackStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface FeedbackRepository extends JpaRepository<Feedback, UUID> {
    // Find by listing
    List<Feedback> findByListingId(UUID listingId);

    // Find by seller
    List<Feedback> findBySellerUserId(UUID sellerUserId);

    // Find by status
    List<Feedback> findByFeedbackStatus(FeedbackStatus status);

    // Find by seller and status
    List<Feedback> findBySellerUserIdAndFeedbackStatus(UUID sellerUserId, FeedbackStatus status);

    // Find by listing and status
    List<Feedback> findByListingIdAndFeedbackStatus(UUID listingId, FeedbackStatus status);

    // Find resubmissions
    List<Feedback> findByIsResubmissionTrue();

    // Find by previous feedback
    List<Feedback> findByPreviousFeedbackId(UUID previousFeedbackId);

    // Find pending reviews (no reviewer assigned)
    List<Feedback> findByFeedbackStatusAndReviewedByStaffIdIsNull(FeedbackStatus status);

    // Find by reviewer
    List<Feedback> findByReviewedByStaffId(UUID staffId);

    // Find created between dates
    List<Feedback> findByCreatedAtBetween(LocalDateTime start, LocalDateTime end);

    // Count by status
    long countByFeedbackStatus(FeedbackStatus status);

    // Count by seller
    long countBySellerUserId(UUID sellerUserId);

    // Check if listing has pending feedback
    boolean existsByListingIdAndFeedbackStatus(UUID listingId, FeedbackStatus status);

    // Custom query: Find latest feedback for each listing
    @Query("SELECT f FROM Feedback f WHERE f.createdAt = " +
            "(SELECT MAX(f2.createdAt) FROM Feedback f2 WHERE f2.listingId = f.listingId)")
    List<Feedback> findLatestFeedbackPerListing();

    // Find with items (fetch join to avoid N+1)
    @Query("SELECT DISTINCT f FROM Feedback f LEFT JOIN FETCH f.feedbackItems WHERE f.feedbackId = :id")
    Optional<Feedback> findByIdWithItems(@Param("id") UUID id);

    // Find with audits (fetch join)
    @Query("SELECT DISTINCT f FROM Feedback f LEFT JOIN FETCH f.feedbackAudits WHERE f.feedbackId = :id")
    Optional<Feedback> findByIdWithAudits(@Param("id") UUID id);

    // Find with all relationships
    @Query("SELECT DISTINCT f FROM Feedback f " +
            "LEFT JOIN FETCH f.feedbackItems " +
            "LEFT JOIN FETCH f.feedbackAudits " +
            "WHERE f.feedbackId = :id")
    Optional<Feedback> findByIdWithAll(@Param("id") UUID id);
}
