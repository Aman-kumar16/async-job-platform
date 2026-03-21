package com.aman.asyncjob.submission.controller;

import com.aman.asyncjob.submission.dto.JobSubmitRequest;
import com.aman.asyncjob.submission.dto.JobSubmitResponse;
import com.aman.asyncjob.submission.service.JobService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/v1/jobs")
@RequiredArgsConstructor
public class JobController {

    private final JobService jobService;

    @PostMapping
    public ResponseEntity<JobSubmitResponse> submitJob(@Valid @RequestBody JobSubmitRequest request) {
        log.info("Received job submission: type={} priority={}", request.getJobType(), request.getPriority());
        JobSubmitResponse response = jobService.submit(request);
        HttpStatus status = response.isDuplicate() ? HttpStatus.OK : HttpStatus.ACCEPTED;
        return ResponseEntity.status(status).body(response);
    }

    @GetMapping("/{jobId}")
    public ResponseEntity<JobSubmitResponse> getJobStatus(@PathVariable String jobId) {
        return ResponseEntity.ok(jobService.getStatus(jobId));
    }
}