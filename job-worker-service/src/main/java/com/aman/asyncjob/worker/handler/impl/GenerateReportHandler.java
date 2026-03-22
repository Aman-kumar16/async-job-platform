package com.aman.asyncjob.worker.handler.impl;

import com.aman.asyncjob.common.dto.JobEvent;
import com.aman.asyncjob.common.enums.JobType;
import com.aman.asyncjob.worker.handler.JobHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class GenerateReportHandler implements JobHandler {

    @Override
    public void handle(JobEvent jobEvent) {
        String reportId = (String) jobEvent.getPayload().getOrDefault("reportId", "unknown");
        String format = (String) jobEvent.getPayload().getOrDefault("format", "PDF");
        log.info("Generating {} report {} for job {}", format, reportId, jobEvent.getJobId());
        //ToDO: real implementation to handle some report generator job.
        try { Thread.sleep(1000); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
        log.info("Report generated successfully for job {}", jobEvent.getJobId());
    }

    @Override
    public JobType getSupportedJobType() {
        return JobType.GENERATE_REPORT;
    }
}