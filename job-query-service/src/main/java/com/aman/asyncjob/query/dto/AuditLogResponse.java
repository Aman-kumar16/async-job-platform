package com.aman.asyncjob.query.dto;

import com.aman.asyncjob.common.enums.JobStatus;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record AuditLogResponse(
        Long id,
        String jobId,
        JobStatus fromStatus,
        JobStatus toStatus,
        String message,
        LocalDateTime recordedAt
) {
}