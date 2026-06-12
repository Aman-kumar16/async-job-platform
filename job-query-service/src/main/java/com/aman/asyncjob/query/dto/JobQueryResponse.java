package com.aman.asyncjob.query.dto;

import com.aman.asyncjob.common.enums.JobPriority;
import com.aman.asyncjob.common.enums.JobStatus;
import com.aman.asyncjob.common.enums.JobType;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record JobQueryResponse(

        String jobId,
        JobType jobType,
        JobStatus jobStatus,
        JobPriority jobPriority,
        int retryCount,
        String errorMessage,
        LocalDateTime submittedAt,
        LocalDateTime updatedAt,
        LocalDateTime completedAt,
        boolean cachedResult
) {
}
