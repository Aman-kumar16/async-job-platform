package com.aman.asyncjob.worker.config;

import com.aman.asyncjob.common.dto.JobEvent;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;

@Configuration
public class KafkaConfig {

    @Bean
    public KafkaTemplate<String, JobEvent> kafkaTemplate(ProducerFactory<String, JobEvent> factory) {
        return new KafkaTemplate<>(factory);
    }
}