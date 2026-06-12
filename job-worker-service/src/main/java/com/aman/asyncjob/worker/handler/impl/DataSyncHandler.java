package com.aman.asyncjob.worker.handler.impl;

import com.aman.asyncjob.common.dto.JobEvent;
import com.aman.asyncjob.common.enums.JobType;
import com.aman.asyncjob.worker.handler.JobHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class DataSyncHandler implements JobHandler {

    @Override
    public void handle(JobEvent jobEvent) {
        String sourceId = (String) jobEvent.payload().getOrDefault("sourceId", "unknown");
        log.info("Syncing data from source {} for job {}", sourceId, jobEvent.jobId());
        //ToDO: real implementation to handle some heavy datasync job.
        try {
            Thread.sleep(800);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        log.info("Data sync completed for job {}", jobEvent.jobId());
    }

    @Override
    public JobType getSupportedJobType() {
        return JobType.DATA_SYNC;
    }
}