package com.aman.asyncjob.worker.service;

import com.aman.asyncjob.common.constants.KafkaTopics;
import com.aman.asyncjob.common.dto.JobEvent;
import com.aman.asyncjob.common.enums.JobStatus;
import com.aman.asyncjob.worker.entity.Job;
import com.aman.asyncjob.worker.entity.JobAuditLog;
import com.aman.asyncjob.worker.handler.JobHandlerRegistry;
import com.aman.asyncjob.worker.repository.JobAuditLogRepository;
import com.aman.asyncjob.worker.repository.JobRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class JobWorkerService {

    private final JobRepository jobRepository;
    private final JobAuditLogRepository auditLogRepository;
    private final JobHandlerRegistry handlerRegistry;
    private final KafkaTemplate<String, JobEvent> kafkaTemplate;

    @Value("${app.retry.max-attempts:3}")
    private int maxAttempts;

    @Value("${app.retry.initial-delay-ms:1000}")
    private long initialDelayMs;

    @Transactional
    public void process(JobEvent jobEvent) {
        String jobId = jobEvent.jobId();

        // Step 1: Fetch job from DB
        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new RuntimeException("Job not found: " + jobId));

        // Step 2: Update status to PROCESSING
        updateJobStatus(job, JobStatus.PROCESSING, "Job picked up by worker");

        try {
            // Step 3: Execute the handler
            handlerRegistry.getHandler(job.getJobType()).handle(jobEvent);

            // Step 4: Success mark as COMPLETED
            updateJobStatus(job, JobStatus.COMPLETED, "Job completed successfully");
            log.info("Job {} completed successfully", jobId);

        } catch (Exception ex) {
            log.error("Job {} failed: {}", jobId, ex.getMessage());
            handleFailure(job, jobEvent, ex);
        }
    }

    private void handleFailure(Job job, JobEvent jobEvent, Exception ex) {
        int newRetryCount = job.getRetryCount() + 1;
        job.setRetryCount(newRetryCount);

        if (newRetryCount >= maxAttempts) {
            // Max retries exhausted send to DLQ
            updateJobStatus(job, JobStatus.DEAD,
                    "Max retries exhausted. Last error: " + ex.getMessage());
            jobRepository.save(job);

            log.warn("Job {} exhausted retries — sending to DLQ", job.getId());
            kafkaTemplate.send(KafkaTopics.JOBS_DLQ, job.getId(), jobEvent);

        } else {
            // Retry with exponential backoff
            long delay = initialDelayMs * (long) Math.pow(2, newRetryCount - 1);
            log.info("Job {} failed — retry {}/{} after {}ms",
                    job.getId(), newRetryCount, maxAttempts, delay);

            updateJobStatus(job, JobStatus.FAILED,
                    "Retry " + newRetryCount + "/" + maxAttempts
                            + ". Error: " + ex.getMessage());
            jobRepository.save(job);

            try { Thread.sleep(delay); } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }

            // Reprocess same event
            process(jobEvent);
        }
    }

    private void updateJobStatus(Job job, JobStatus newStatus, String message) {
        JobStatus oldStatus = job.getJobStatus();
        job.setJobStatus(newStatus);
        job.setUpdatedAt(LocalDateTime.now());
        jobRepository.save(job);

        JobAuditLog log = JobAuditLog.builder()
                .jobId(job.getId())
                .fromStatus(oldStatus)
                .toStatus(newStatus)
                .message(message)
                .build();
        auditLogRepository.save(log);
    }
}