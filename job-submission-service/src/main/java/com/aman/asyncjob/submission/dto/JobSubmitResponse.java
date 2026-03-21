package com.aman.asyncjob.submission.dto;

import com.aman.asyncjob.common.enums.JobPriority;
import com.aman.asyncjob.common.enums.JobStatus;
import com.aman.asyncjob.common.enums.JobType;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class JobSubmitResponse {

    private String jobId;
    private JobStatus jobStatus;
    private JobType jobType;
    private JobPriority jobPriority;
    private LocalDateTime submittedAt;
    private boolean duplicate;
}