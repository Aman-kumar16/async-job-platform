package com.aman.asyncjob.worker.service;

import com.aman.asyncjob.common.constants.KafkaTopics;
import com.aman.asyncjob.common.dto.JobEvent;
import com.aman.asyncjob.common.enums.JobStatus;
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
@RequiredArgsConstructor
public class JobRetryService {

    private final JobRepository jobRepository;
    private final KafkaTemplate<String, JobEvent> kafkaTemplate;

    @Transactional
    public String requeueDeadJob(String jobId) {
        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new RuntimeException("Job not found: " + jobId));

        // Only DEAD jobs can be manually retried
        if (job.getJobStatus() != JobStatus.DEAD) {
            return "Job is not in DEAD status current status: " + job.getJobStatus();
        }

        // Reset job for reprocessing
        job.setJobStatus(JobStatus.PENDING);
        job.setRetryCount(0);
        job.setErrorMessage(null);
        jobRepository.save(job);

        // Republish to correct priority topic
        JobEvent event = JobEvent.builder()
                .jobId(job.getId())
                .jobType(job.getJobType())
                .jobPriority(job.getJobPriority())
                .payload(job.getPayload())
                .retryCount(0)
                .build();

        String topic = fetchTopic(job);

        //ToDo: May run into issue where kafka gets down after it has been saved in db and failed to publish.
        TransactionSynchronizationManager.registerSynchronization(
                new TransactionSynchronization() {
                    @Override
                    public void afterCommit() {
                        kafkaTemplate.send(topic, job.getId(), event);
                        log.info("Dead job {} manually requeued to topic {}", jobId, topic);
                    }
                }
        );
        return "Job " + jobId + " requeued successfully to topic " + topic;
    }

    private static String fetchTopic(Job job) {
        return switch (job.getJobPriority()) {
            case HIGH   -> KafkaTopics.JOBS_HIGH;
            case MEDIUM -> KafkaTopics.JOBS_MEDIUM;
            case LOW    -> KafkaTopics.JOBS_LOW;
        };
    }
}
