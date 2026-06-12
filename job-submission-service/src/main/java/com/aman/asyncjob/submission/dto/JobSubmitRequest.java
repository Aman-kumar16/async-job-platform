package com.aman.asyncjob.submission.dto;

import com.aman.asyncjob.common.enums.JobPriority;
import com.aman.asyncjob.common.enums.JobType;
import jakarta.validation.constraints.NotNull;

import java.util.Map;

public record JobSubmitRequest(

        @NotNull(message = "jobType is required")
        JobType jobType,

        @NotNull(message = "priority is required")
        JobPriority priority,

        Map<String, Object> payload,

        String idempotencyKey
) {
}