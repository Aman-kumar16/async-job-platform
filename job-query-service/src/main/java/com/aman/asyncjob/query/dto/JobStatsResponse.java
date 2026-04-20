package com.aman.asyncjob.query.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class JobStatsResponse {

    private long totalJobs;
    private long pendingJobs;
    private long processingJobs;
    private long completedJobs;
    private long failedJobs;
    private long deadJobs;
}