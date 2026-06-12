package com.aman.asyncjob.submission.repository;

import com.aman.asyncjob.submission.entity.Job;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface JobRepository extends JpaRepository<Job, String> {

    Optional<Job> findByIdempotencyKey(String idempotencyKey);
}
