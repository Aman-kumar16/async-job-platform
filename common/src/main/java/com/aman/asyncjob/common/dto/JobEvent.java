package com.aman.asyncjob.common.dto;

import com.aman.asyncjob.common.enums.JobPriority;
import com.aman.asyncjob.common.enums.JobType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JobEvent {

    private String jobId;
    private JobType jobType;
    private JobPriority priority;

    // Keeping it flexible payload so that each job type defines its own keys
    // e.g. SEND_EMAIL: {"to": "user@example.com", "subject": "Hello"}
    private Map<String, Object> payload;

    private int retryCount;
    private String idempotencyKey;
    private LocalDateTime submittedAt;
}