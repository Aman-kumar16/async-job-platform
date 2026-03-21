package com.aman.asyncjob.submission.repository;

import com.aman.asyncjob.submission.entity.Job;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface JobRepository extends JpaRepository<Job,String> {

    Optional<Job> findByIdempotencyKey(String idempotencyKey);
}
