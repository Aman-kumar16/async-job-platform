package com.aman.asyncjob.worker.handler.impl;

import com.aman.asyncjob.common.dto.JobEvent;
import com.aman.asyncjob.common.enums.JobType;
import com.aman.asyncjob.worker.handler.JobHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class SendEmailHandler implements JobHandler {

    @Override
    public void handle(JobEvent jobEvent) {
        // Simulated — in production this would call an email service
        String to = (String) jobEvent.payload().getOrDefault("to", "unknown");
        String subject = (String) jobEvent.payload().getOrDefault("subject", "no subject");
        log.info("Sending email to {} with subject '{}' for job {}",
                to, subject, jobEvent.jobId());
        //ToDO: real implementation to handle sending emails to provided email ids.
        // Simulate processing time
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        log.info("Email sent successfully for job {}", jobEvent.jobId());
    }

    @Override
    public JobType getSupportedJobType() {
        return JobType.SEND_EMAIL;
    }
}