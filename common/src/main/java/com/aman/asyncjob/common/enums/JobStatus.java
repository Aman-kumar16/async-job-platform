package com.aman.asyncjob.common.enums;

/**
 * Represents the lifecycle state of an asynchronous job.
 */
public enum JobStatus {
    PENDING,
    PROCESSING,
    COMPLETED,
    FAILED,
    DEAD
}
