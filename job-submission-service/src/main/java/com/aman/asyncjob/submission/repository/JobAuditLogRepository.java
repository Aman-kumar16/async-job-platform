package com.aman.asyncjob.submission.repository;

import com.aman.asyncjob.submission.entity.JobAuditLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface JobAuditLogRepository extends JpaRepository<JobAuditLog, Long> {

    List<JobAuditLog> findByJobIdOrderByRecordedAtAsc(String jobId);
}
