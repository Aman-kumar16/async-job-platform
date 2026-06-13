package com.aman.asyncjob.worker.dto;

import com.aman.asyncjob.common.enums.JobPriority;
import com.aman.asyncjob.common.enums.JobStatus;
import com.aman.asyncjob.common.enums.JobType;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record JobRetryResponse(
        String jobId,
        JobStatus jobStatus,
        JobType jobType,
        JobPriority jobPriority,
        LocalDateTime submittedAt,
        boolean isRetried,
        String message
) {
}