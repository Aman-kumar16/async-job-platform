package com.aman.asyncjob.submission.dto;

import com.aman.asyncjob.common.enums.JobPriority;
import com.aman.asyncjob.common.enums.JobStatus;
import com.aman.asyncjob.common.enums.JobType;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record JobSubmitResponse(
        String jobId,
        JobStatus jobStatus,
        JobType jobType,
        JobPriority jobPriority,
        LocalDateTime submittedAt,
        boolean isDuplicate
) {
}