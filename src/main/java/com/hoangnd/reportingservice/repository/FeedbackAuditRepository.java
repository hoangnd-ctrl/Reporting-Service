package com.hoangnd.reportingservice.repository;

import com.hoangnd.reportingservice.model.entity.FeedbackAudit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface FeedbackAuditRepository extends JpaRepository<FeedbackAudit, UUID> {
}
