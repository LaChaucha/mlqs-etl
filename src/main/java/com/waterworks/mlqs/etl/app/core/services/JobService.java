package com.waterworks.mlqs.etl.app.core.services;

import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class JobService {
  private final JobLauncher jobLauncher;
  private final Job etlJob;
  private final Map<String, JobExecution> jobExecutionCache;

  @Autowired
  public JobService(final JobLauncher jobLauncher,
                    final Job etlJob,
                    Map<String, JobExecution> jobExecutionCache
  ) {
    this.jobLauncher = jobLauncher;
    this.etlJob = etlJob;
    this.jobExecutionCache = jobExecutionCache;
  }

  public void executeJob(final String jobId) {
    JobParameters jobParameters = new JobParametersBuilder()
        .addString("jobId", jobId)
        .toJobParameters();

    try {
      jobExecutionCache.put(jobId, jobLauncher.run(etlJob, jobParameters));

    } catch (JobExecutionAlreadyRunningException | JobRestartException |
             JobInstanceAlreadyCompleteException | JobParametersInvalidException e) {
      throw new RuntimeException(e);
    }
  }
}