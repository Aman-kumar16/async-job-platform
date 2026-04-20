package com.aman.asyncjob.query.dto;

import com.aman.asyncjob.common.enums.JobPriority;
import com.aman.asyncjob.common.enums.JobStatus;
import com.aman.asyncjob.common.enums.JobType;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class JobQueryResponse {

    private String jobId;
    private JobType jobType;
    private JobStatus jobStatus;
    private JobPriority jobPriority;
    private int retryCount;
    private String errorMessage;
    private LocalDateTime submittedAt;
    private LocalDateTime updatedAt;
    private LocalDateTime completedAt;
    private boolean cachedResult;
}
