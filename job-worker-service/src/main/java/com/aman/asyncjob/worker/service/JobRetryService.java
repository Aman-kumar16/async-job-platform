package com.aman.asyncjob.worker.service;

import com.aman.asyncjob.common.constants.KafkaTopics;
import com.aman.asyncjob.common.dto.JobEvent;
import com.aman.asyncjob.common.enums.JobStatus;
import com.aman.asyncjob.worker.dto.JobRetryResponse;
import com.aman.asyncjob.worker.entity.Job;
import com.aman.asyncjob.worker.repository.JobRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class JobRetryService {

    private final JobRepository jobRepository;
    private final KafkaTemplate<String, JobEvent> kafkaTemplate;

    public JobRetryResponse requeueDeadJob(String jobId) {
        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new RuntimeException("Job not found: " + jobId));

        // Only DEAD jobs can be manually retried
        if (job.getJobStatus() != JobStatus.DEAD) {
            return JobRetryResponse.builder()
                    .jobId(job.getId())
                    .jobStatus(job.getJobStatus())
                    .jobType(job.getJobType())
                    .jobPriority(job.getJobPriority())
                    .submittedAt(job.getSubmittedAt())
                    .isRetried(false)
                    .message("Only DEAD jobs can be retried manually.")
                    .build();
        }

        // Reset job for reprocessing
        job.setJobStatus(JobStatus.PENDING);
        job.setRetryCount(0);
        job.setErrorMessage(null);
        Job updatedJob = jobRepository.save(job);

        // Republish to correct priority topic
        JobEvent event = JobEvent.builder()
                .jobId(updatedJob.getId())
                .jobType(updatedJob.getJobType())
                .jobPriority(updatedJob.getJobPriority())
                .payload(updatedJob.getPayload())
                .retryCount(0)
                .build();

        String topic = fetchTopic(job);

        //ToDo: May run into issue where kafka gets down after it has been saved in db and failed to publish.
        TransactionSynchronizationManager.registerSynchronization(
                new TransactionSynchronization() {
                    @Override
                    public void afterCommit() {
                        kafkaTemplate.send(topic, updatedJob.getId(), event);
                        log.info("Dead job {} manually requeued to topic {}", updatedJob, topic);
                    }
                }
        );
        return JobRetryResponse.builder()
                .jobId(updatedJob.getId())
                .jobStatus(updatedJob.getJobStatus())
                .jobType(updatedJob.getJobType())
                .jobPriority(updatedJob.getJobPriority())
                .submittedAt(updatedJob.getSubmittedAt())
                .isRetried(true)
                .message("Job requeued successfully.")
                .build();
    }

    private static String fetchTopic(Job job) {
        return switch (job.getJobPriority()) {
            case HIGH -> KafkaTopics.JOBS_HIGH;
            case MEDIUM -> KafkaTopics.JOBS_MEDIUM;
            case LOW -> KafkaTopics.JOBS_LOW;
        };
    }
}
