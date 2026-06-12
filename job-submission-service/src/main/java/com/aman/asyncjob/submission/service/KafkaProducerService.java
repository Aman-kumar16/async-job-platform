package com.aman.asyncjob.submission.service;

import com.aman.asyncjob.common.constants.KafkaTopics;
import com.aman.asyncjob.common.dto.JobEvent;
import com.aman.asyncjob.common.enums.JobPriority;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class KafkaProducerService {

    private final KafkaTemplate<String, JobEvent> kafkaTemplate;

    public void publishJob(JobEvent jobEvent) {
        String topic = resolveTopic(jobEvent.jobPriority());

        kafkaTemplate.send(topic, jobEvent.jobId(), jobEvent)
                .whenComplete((result, exception) -> {
                    if (exception != null) {
                        log.error("Failed to publish job {} to topic {}: {}",
                                jobEvent.jobId(), topic, exception.getMessage());
                    } else {
                        log.info("Job {} published to topic {} partition {} offset {}",
                                jobEvent.jobId(),
                                topic,
                                result.getRecordMetadata().partition(),
                                result.getRecordMetadata().offset());
                    }
                });
    }

    private String resolveTopic(JobPriority priority) {
        return switch (priority) {
            case HIGH -> KafkaTopics.JOBS_HIGH;
            case MEDIUM -> KafkaTopics.JOBS_MEDIUM;
            case LOW -> KafkaTopics.JOBS_LOW;
        };
    }
}
