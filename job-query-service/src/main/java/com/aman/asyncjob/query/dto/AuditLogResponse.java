package com.aman.asyncjob.query.dto;

import com.aman.asyncjob.common.enums.JobStatus;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class AuditLogResponse {

    private Long id;
    private String jobId;
    private JobStatus fromStatus;
    private JobStatus toStatus;
    private String message;
    private LocalDateTime recordedAt;
}