package com.aman.asyncjob.query.service;

import com.aman.asyncjob.common.constants.KafkaTopics;
import com.aman.asyncjob.common.enums.JobStatus;
import com.aman.asyncjob.query.dto.AuditLogResponse;
import com.aman.asyncjob.query.dto.JobQueryResponse;
import com.aman.asyncjob.query.dto.JobStatsResponse;
import com.aman.asyncjob.query.entity.Job;
import com.aman.asyncjob.query.repository.JobAuditLogRepository;
import com.aman.asyncjob.query.repository.JobRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class JobQueryService {

    private final JobRepository jobRepository;
    private final JobAuditLogRepository auditLogRepository;
    private final StringRedisTemplate redisTemplate;

    public JobQueryResponse getJobById(String jobId) {
        // Check Redis cache first
        String redisKey = KafkaTopics.REDIS_JOB_STATUS_PREFIX + jobId;
        String cachedStatus = redisTemplate.opsForValue().get(redisKey);

        if (cachedStatus != null) {
            log.info("Cache hit for job {}", jobId);
            // Still fetch full details from DB but flag it as cached
            return jobRepository.findById(jobId)
                    .map(job -> mapToResponse(job, true))
                    .orElseThrow(() -> new EntityNotFoundException("Job not found: " + jobId));
        }

        log.info("Cache miss for job {} — hitting PostgreSQL", jobId);
        return jobRepository.findById(jobId)
                .map(job -> mapToResponse(job, false))
                .orElseThrow(() -> new EntityNotFoundException("Job not found: " + jobId));
    }

    public List<JobQueryResponse> getJobsByStatus(JobStatus status) {
        return jobRepository.findAll().stream()
                .filter(job -> job.getJobStatus() == status)
                .map(job -> mapToResponse(job, false))
                .collect(Collectors.toList());
    }

    public List<AuditLogResponse> getAuditLog(String jobId) {
        // Verify job exists first
        jobRepository.findById(jobId)
                .orElseThrow(() -> new EntityNotFoundException("Job not found: " + jobId));

        return auditLogRepository.findByJobIdOrderByRecordedAtAsc(jobId)
                .stream()
                .map(log -> AuditLogResponse.builder()
                        .id(log.getId())
                        .jobId(log.getJobId())
                        .fromStatus(log.getFromStatus())
                        .toStatus(log.getToStatus())
                        .message(log.getMessage())
                        .recordedAt(log.getRecordedAt())
                        .build())
                .collect(Collectors.toList());
    }

    public JobStatsResponse getStats() {
        List<Job> allJobs = jobRepository.findAll();

        return JobStatsResponse.builder()
                .totalJobs(allJobs.size())
                .pendingJobs(countByStatus(allJobs, JobStatus.PENDING))
                .processingJobs(countByStatus(allJobs, JobStatus.PROCESSING))
                .completedJobs(countByStatus(allJobs, JobStatus.COMPLETED))
                .failedJobs(countByStatus(allJobs, JobStatus.FAILED))
                .deadJobs(countByStatus(allJobs, JobStatus.DEAD))
                .build();
    }

    private long countByStatus(List<Job> jobs, JobStatus status) {
        return jobs.stream().filter(j -> j.getJobStatus() == status).count();
    }

    private JobQueryResponse mapToResponse(Job job, boolean cached) {
        return JobQueryResponse.builder()
                .jobId(job.getId())
                .jobType(job.getJobType())
                .jobStatus(job.getJobStatus())
                .jobPriority(job.getJobPriority())
                .retryCount(job.getRetryCount())
                .errorMessage(job.getErrorMessage())
                .submittedAt(job.getSubmittedAt())
                .updatedAt(job.getUpdatedAt())
                .completedAt(job.getCompletedAt())
                .cachedResult(cached)
                .build();
    }
}