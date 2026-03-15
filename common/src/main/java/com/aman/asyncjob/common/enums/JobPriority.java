package com.aman.asyncjob.common.enums;

/**
 * Defines execution priority. Used to route jobs to specific Kafka partitions/topics.
 */
public enum JobPriority {
    HIGH,
    MEDIUM,
    LOW
}
