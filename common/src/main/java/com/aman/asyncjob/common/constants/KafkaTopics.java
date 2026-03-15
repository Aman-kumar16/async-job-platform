package com.aman.asyncjob.common.constants;

public class KafkaTopics {

    private KafkaTopics() {}

    // Priorities for Kafka queue
    public static final String JOBS_HIGH   = "jobs.high";
    public static final String JOBS_MEDIUM = "jobs.medium";
    public static final String JOBS_LOW    = "jobs.low";
    public static final String JOBS_DLQ    = "jobs.dead-letter";

    // Redis key prefixes
    public static final String REDIS_IDEMPOTENCY_PREFIX = "idempotency:";
    public static final String REDIS_JOB_STATUS_PREFIX  = "job:status:";
}
