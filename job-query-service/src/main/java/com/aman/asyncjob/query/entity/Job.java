package com.aman.asyncjob.query.entity;

import com.aman.asyncjob.common.enums.JobPriority;
import com.aman.asyncjob.common.enums.JobStatus;
import com.aman.asyncjob.common.enums.JobType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.Map;

@Entity
@Table(name = "jobs")
@Getter
@Setter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@ToString(onlyExplicitlyIncluded = true)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Job {

    @Id
    @ToString.Include
    @EqualsAndHashCode.Include
    private String id;

    @Enumerated(EnumType.STRING)
    @Column(name = "job_type", nullable = false)
    @ToString.Include
    private JobType jobType;

    @Enumerated(EnumType.STRING)
    @Column(name = "job_priority", nullable = false)
    @ToString.Include
    private JobPriority jobPriority;

    @Enumerated(EnumType.STRING)
    @Column(name = "job_status", nullable = false)
    @ToString.Include
    private JobStatus jobStatus;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private Map<String, Object> payload;

    @Column(name = "idempotency_key", unique = true)
    @ToString.Include
    private String idempotencyKey;

    @Column(name = "retry_count")
    @ToString.Include
    private int retryCount;

    @Column(name = "max_retries")
    private int maxRetries;

    @Column(name = "error_message")
    private String errorMessage;

    @Version
    private Long version;

    @CreationTimestamp
    @Column(name = "submitted_at", nullable = false)
    @ToString.Include
    private LocalDateTime submittedAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    @ToString.Include
    private LocalDateTime updatedAt;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;
}
