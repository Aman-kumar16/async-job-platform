package com.aman.asyncjob.query.controller;

import com.aman.asyncjob.common.enums.JobStatus;
import com.aman.asyncjob.query.dto.AuditLogResponse;
import com.aman.asyncjob.query.dto.JobQueryResponse;
import com.aman.asyncjob.query.dto.JobStatsResponse;
import com.aman.asyncjob.query.service.JobQueryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/jobs")
@RequiredArgsConstructor
public class JobQueryController {

    private final JobQueryService jobQueryService;

    @GetMapping("/{jobId}")
    public ResponseEntity<JobQueryResponse> getJobById(@PathVariable String jobId) {
        return ResponseEntity.ok(jobQueryService.getJobById(jobId));
    }

    @GetMapping
    public ResponseEntity<List<JobQueryResponse>> getJobsByStatus(
            @RequestParam(required = false) JobStatus status) {
        return ResponseEntity.ok(jobQueryService.getJobsByStatus(status));
    }

    @GetMapping("/{jobId}/audit")
    public ResponseEntity<List<AuditLogResponse>> getAuditLog(@PathVariable String jobId) {
        return ResponseEntity.ok(jobQueryService.getAuditLog(jobId));
    }

    @GetMapping("/stats")
    public ResponseEntity<JobStatsResponse> getStats() {
        return ResponseEntity.ok(jobQueryService.getStats());
    }
}