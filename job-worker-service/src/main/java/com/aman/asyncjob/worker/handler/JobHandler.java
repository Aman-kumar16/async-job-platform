package com.aman.asyncjob.worker.handler;

import com.aman.asyncjob.common.dto.JobEvent;
import com.aman.asyncjob.common.enums.JobType;

public interface JobHandler {

    /**
     * Executes the job logic.
     * Each job type should implement this interface.
     * Throw exception to trigger retry logic.
     */
    void handle(JobEvent jobEvent);

    /**
     * Returns the job type this handler is responsible for.
     * Used by the worker to look up the correct handler at runtime.
     */
    JobType getSupportedJobType();
}
