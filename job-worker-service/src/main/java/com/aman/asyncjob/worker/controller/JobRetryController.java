package com.aman.asyncjob.worker.controller;

import com.aman.asyncjob.worker.dto.JobRetryResponse;
import com.aman.asyncjob.worker.service.JobRetryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/v1/jobs")
@RequiredArgsConstructor
public class JobRetryController {

    private final JobRetryService jobRetryService;

    @PostMapping("/{jobId}/retry")
    public ResponseEntity<JobRetryResponse> retryDeadJob(@PathVariable String jobId) {
        var response = jobRetryService.requeueDeadJob(jobId);
        if (response.isRetried()) {
            return ResponseEntity.ok(response);
        }
        return ResponseEntity.badRequest().body(response);
    }
}