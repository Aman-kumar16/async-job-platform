package com.aman.asyncjob.worker.handler.impl;

import com.aman.asyncjob.common.dto.JobEvent;
import com.aman.asyncjob.common.enums.JobType;
import com.aman.asyncjob.worker.handler.JobHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class FailAlwaysHandler implements JobHandler {

    @Override
    public void handle(JobEvent jobEvent) {
        log.info("FailAlwaysHandler triggered for job {} — this will always fail", jobEvent.getJobId());
        throw new RuntimeException("Intentional failure for testing DLQ");
    }

    @Override
    public JobType getSupportedJobType() {
        return JobType.FAIL_ALWAYS;
    }
}