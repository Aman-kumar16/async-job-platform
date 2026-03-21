package com.aman.asyncjob.submission.service;

import com.aman.asyncjob.common.dto.JobEvent;
import com.aman.asyncjob.common.enums.JobStatus;
import com.aman.asyncjob.submission.dto.JobSubmitRequest;
import com.aman.asyncjob.submission.dto.JobSubmitResponse;
import com.aman.asyncjob.submission.entity.Job;
import com.aman.asyncjob.submission.entity.JobAuditLog;
import com.aman.asyncjob.submission.repository.JobAuditLogRepository;
import com.aman.asyncjob.submission.repository.JobRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.util.StringUtils;

import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class JobService {

    private final JobRepository jobRepository;
    private final JobAuditLogRepository auditLogRepository;
    private final IdempotencyService idempotencyService;
    private final KafkaProducerService kafkaProducerService;

    private static final int DEFAULT_MAX_RETRIES = 3;

    @Transactional
    public JobSubmitResponse submit(JobSubmitRequest request) {
        String jobId = UUID.randomUUID().toString();

        // Step 1: Idempotency check
        if (StringUtils.hasText(request.getIdempotencyKey())) {
            Optional<String> existingJobId = idempotencyService
                    .registerOrGetExisting(request.getIdempotencyKey(), jobId);

            if (existingJobId.isPresent()) {
                return buildDuplicateResponse(existingJobId.get());
            }
        }

        // Step 2: Persist job to PostgreSQL
        Job job = Job.builder()
                .id(jobId)
                .jobType(request.getJobType())
                .jobPriority(request.getPriority())
                .jobStatus(JobStatus.PENDING)
                .payload(request.getPayload())
                .idempotencyKey(request.getIdempotencyKey())
                .retryCount(0)
                .maxRetries(DEFAULT_MAX_RETRIES)
                .build();

        jobRepository.save(job);
        log.info("Job persisted: id={} type={} priority={}", jobId, job.getJobType(), job.getJobPriority());

        // Step 3: Write audit log
        writeAuditLog(jobId, null, JobStatus.PENDING, "Job submitted");

        // Step 4: Cache status in Redis
        idempotencyService.cacheJobStatus(jobId, JobStatus.PENDING.name());

        // Step 5: Publish to Kafka
        JobEvent event = JobEvent.builder()
                .jobId(jobId)
                .jobType(request.getJobType())
                .priority(request.getPriority())
                .payload(request.getPayload())
                .retryCount(0)
                .idempotencyKey(request.getIdempotencyKey())
                .build();

        // Wrapping our Kafka call so that it doesn't publish until database has committed to keep it consistent
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCommit() {
                kafkaProducerService.publishJob(event);
            }
        });

        // Step 6: Return
        return JobSubmitResponse.builder()
                .jobId(jobId)
                .jobStatus(JobStatus.PENDING)
                .jobType(request.getJobType())
                .jobPriority(request.getPriority())
                .duplicate(false)
                .build();
    }

    public JobSubmitResponse getStatus(String jobId) {
        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new RuntimeException("Job not found: " + jobId));

        return JobSubmitResponse.builder()
                .jobId(job.getId())
                .jobStatus(job.getJobStatus())
                .jobType(job.getJobType())
                .jobPriority(job.getJobPriority())
                .submittedAt(job.getSubmittedAt())
                .duplicate(false)
                .build();
    }

    private JobSubmitResponse buildDuplicateResponse(String existingJobId) {
        return jobRepository.findById(existingJobId)
                .map(job -> JobSubmitResponse.builder()
                        .jobId(job.getId())
                        .jobStatus(job.getJobStatus())
                        .jobType(job.getJobType())
                        .jobPriority(job.getJobPriority())
                        .submittedAt(job.getSubmittedAt())
                        .duplicate(true)
                        .build())
                .orElseThrow(() -> new RuntimeException(
                        "Idempotency key points to missing job: " + existingJobId));
    }

    private void writeAuditLog(String jobId, JobStatus from, JobStatus to, String message) {
        JobAuditLog entry = JobAuditLog.builder()
                .jobId(jobId)
                .fromStatus(from)
                .toStatus(to)
                .message(message)
                .build();
        auditLogRepository.save(entry);
    }
}