package com.aman.asyncjob.submission.service;

import com.aman.asyncjob.common.constants.KafkaTopics;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class IdempotencyService {

    private final StringRedisTemplate redisTemplate;

    @Value("${app.idempotency.ttl-hours:24}")
    private long ttlHours;

    public Optional<String> registerOrGetExisting(String idempotencyKey, String jobId) {
        String redisKey = KafkaTopics.REDIS_IDEMPOTENCY_PREFIX + idempotencyKey;

        Boolean isNew = redisTemplate.opsForValue()
                .setIfAbsent(redisKey,jobId, Duration.ofHours(ttlHours));

        if(Boolean.TRUE.equals(isNew)){
            log.debug("Idempotency key registered: {} -> {}", idempotencyKey, jobId);
            return Optional.empty();
        }

        String existingJobId = redisTemplate.opsForValue().get(redisKey);
        log.info("Duplicate submission detected for key: {} -> existing jobId: {}", idempotencyKey, existingJobId);
        return Optional.ofNullable(existingJobId);
    }

    public void cacheJobStatus(String jobId, String status) {
        String redisKey = KafkaTopics.REDIS_JOB_STATUS_PREFIX + jobId;
        redisTemplate.opsForValue().set(redisKey, status, Duration.ofDays(7));
    }

    public Optional<String> getCachedStatus(String jobId) {
        String redisKey = KafkaTopics.REDIS_JOB_STATUS_PREFIX + jobId;
        return Optional.ofNullable(redisTemplate.opsForValue().get(redisKey));
    }
}


