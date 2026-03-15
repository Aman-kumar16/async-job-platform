package com.aman.asyncjob.common.enums;

/**
 * Defines the specific business logic handler that should execute the job.
 */
public enum JobType {
    SEND_EMAIL,
    GENERATE_REPORT,
    DATA_SYNC,
    FAIL_ALWAYS
}
