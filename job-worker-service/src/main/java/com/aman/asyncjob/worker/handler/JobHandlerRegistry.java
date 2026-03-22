package com.aman.asyncjob.worker.handler;

import com.aman.asyncjob.common.enums.JobType;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Registry that holds all JobHandler implementations.
 * At runtime, worker looks up the correct handler by job type
 * Since Spring automatically injects all beans implementing JobHandler.
 * <p>
 * To Add a new job type just create a new handler class.
 */
@Component
public class JobHandlerRegistry {

    private final Map<JobType, JobHandler> handlers;

    public JobHandlerRegistry(List<JobHandler> handlerList) {
        this.handlers = handlerList.stream()
                .collect(Collectors.toMap(JobHandler::getSupportedJobType, Function.identity()));
    }

    public JobHandler getHandler(JobType jobType) {
        JobHandler handler = handlers.get(jobType);
        if (handler == null) {
            throw new RuntimeException("No handler found for job type: " + jobType);
        }
        return handler;
    }
}