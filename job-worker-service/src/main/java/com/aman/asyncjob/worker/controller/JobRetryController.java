package com.aman.asyncjob.worker.controller;

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

    //ToDo: implement this properly with DTO response once happy path works fine.
    @PostMapping("/{jobId}/retry")
    public ResponseEntity<String> retryDeadJob(@PathVariable String jobId) {
        String response = jobRetryService.requeueDeadJob(jobId);
        if (response.contains("success")) {
            return ResponseEntity.ok(response);
        }
        return ResponseEntity.badRequest()
                .body(response);
    }
}