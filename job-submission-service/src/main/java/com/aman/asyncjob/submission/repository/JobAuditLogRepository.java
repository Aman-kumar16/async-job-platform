package com.aman.asyncjob.submission.repository;

import com.aman.asyncjob.submission.entity.JobAuditLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface JobAuditLogRepository extends JpaRepository<JobAuditLog, Long> {

    List<JobAuditLog> findByJobIdOrderByRecordedAtAsc(String jobId);
}
