package com.aman.asyncjob.worker.consumer;

import com.aman.asyncjob.common.constants.KafkaTopics;
import com.aman.asyncjob.common.dto.JobEvent;
import com.aman.asyncjob.worker.service.JobWorkerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class JobKafkaConsumer {

    private final JobWorkerService jobWorkerService;

    @KafkaListener(
            topics = KafkaTopics.JOBS_HIGH,
            groupId = "job-worker-group",
            concurrency = "3"
    )
    public void consumeHigh(JobEvent jobEvent) {
        log.info("Consumed HIGH priority job: {}", jobEvent.getJobId());
        jobWorkerService.process(jobEvent);
    }

    @KafkaListener(
            topics = KafkaTopics.JOBS_MEDIUM,
            groupId = "job-worker-group",
            concurrency = "2"
    )
    public void consumeMedium(JobEvent jobEvent) {
        log.info("Consumed MEDIUM priority job: {}", jobEvent.getJobId());
        jobWorkerService.process(jobEvent);
    }

    @KafkaListener(
            topics = KafkaTopics.JOBS_LOW,
            groupId = "job-worker-group",
            concurrency = "1"
    )
    public void consumeLow(JobEvent jobEvent) {
        log.info("Consumed LOW priority job: {}", jobEvent.getJobId());
        jobWorkerService.process(jobEvent);
    }
}