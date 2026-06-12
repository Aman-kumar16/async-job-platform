package com.aman.asyncjob.submission.controller;

import com.aman.asyncjob.submission.dto.JobSubmitRequest;
import com.aman.asyncjob.submission.dto.JobSubmitResponse;
import com.aman.asyncjob.submission.service.JobService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Job Submission", description = "Endpoints for creating and managing async jobs")
public class JobController {

    private final JobService jobService;

    @Operation(
            summary = "Submit a new background job",
            description = "Validates the request and pushes the job to the processing queue. Returns 202 if accepted, or 200 if it's a isDuplicate request."
    )
    @ApiResponse(responseCode = "202", description = "Job accepted and queued")
    @ApiResponse(responseCode = "200", description = "Duplicate request detected; returned existing job status")
    @ApiResponse(responseCode = "400", description = "Invalid job submission payload")
    @PostMapping
    public ResponseEntity<JobSubmitResponse> submitJob(@Valid @RequestBody JobSubmitRequest request) {
        log.info("Received job submission: type={} priority={}", request.jobType(), request.priority());
        JobSubmitResponse response = jobService.submit(request);
        HttpStatus status = response.isDuplicate() ? HttpStatus.OK : HttpStatus.ACCEPTED;
        return ResponseEntity.status(status).body(response);
    }

    @Operation(
            summary = "Fetch current status of a job",
            description = "Returns the processing state, priority and metadata for a specific Job ID."
    )
    @ApiResponse(responseCode = "200", description = "Job found and status returned")
    @ApiResponse(responseCode = "404", description = "Job ID not found in database or cache")
    @GetMapping("/{jobId}")
    public ResponseEntity<JobSubmitResponse> getJobStatus(@PathVariable String jobId) {
        return ResponseEntity.ok(jobService.getStatus(jobId));
    }
}