package com.aman.asyncjob.submission.config;

import com.aman.asyncjob.common.dto.JobEvent;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;

import static com.aman.asyncjob.common.constants.KafkaTopics.*;

@Configuration
public class KafkaConfig {

    @Bean
    public KafkaTemplate<String, JobEvent> kafkaTemplate(ProducerFactory<String, JobEvent> factory) {
        return new KafkaTemplate<>(factory);
    }

    @Bean
    public NewTopic topicHigh() {
        return TopicBuilder.name(JOBS_HIGH)
                .partitions(3)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic topicMedium() {
        return TopicBuilder.name(JOBS_MEDIUM)
                .partitions(3)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic topicLow() {
        return TopicBuilder.name(JOBS_LOW)
                .partitions(3)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic topicDlq() {
        return TopicBuilder.name(JOBS_DLQ)
                .partitions(1)
                .replicas(1)
                .build();
    }
}