package com.aman.asyncjob.submission.dto;

import com.aman.asyncjob.common.enums.JobPriority;
import com.aman.asyncjob.common.enums.JobType;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.Map;

@Data
public class JobSubmitRequest {

    @NotNull(message = "jobType is required")
    private JobType jobType;

    @NotNull(message = "priority is required")
    private JobPriority priority;

    private Map<String, Object> payload;

    private String idempotencyKey;
}