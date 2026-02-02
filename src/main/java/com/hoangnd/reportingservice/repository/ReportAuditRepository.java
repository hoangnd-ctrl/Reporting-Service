package com.hoangnd.reportingservice.repository;

import com.hoangnd.reportingservice.model.entity.ReportAudit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ReportAuditRepository extends JpaRepository<ReportAudit, UUID> {
}
