package com.aman.asyncjob.common.dto;

import com.aman.asyncjob.common.enums.JobPriority;
import com.aman.asyncjob.common.enums.JobType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;

@Builder
public record JobEvent(
        String jobId,
        JobType jobType,
        JobPriority jobPriority,
        Map<String, Object> payload,
        int retryCount,
        String idempotencyKey,
        LocalDateTime submittedAt
) {
}