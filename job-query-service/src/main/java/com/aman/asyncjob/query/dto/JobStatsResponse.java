package com.aman.asyncjob.query.dto;

import lombok.Builder;

@Builder
public record JobStatsResponse(
        long totalJobs,
        long pendingJobs,
        long processingJobs,
        long completedJobs,
        long failedJobs,
        long deadJobs
) {
}