package com.waterworks.mlqs.etl.app.core.services;

import java.util.Map;
import org.springframework.batch.core.JobExecution;
import org.springframework.stereotype.Service;

@Service
public class GetJobStatusService {
  private final Map<String, JobExecution> jobExecutionCache;

  public GetJobStatusService(final Map<String, JobExecution> jobExecutionCache) {
    this.jobExecutionCache = jobExecutionCache;
  }

  public String getStatusByJobId(final String jobId) {
    if (!jobExecutionCache.containsKey(jobId)) {
      return "Invalid jobId.";
    } else {
      return String.valueOf(jobExecutionCache.get(jobId).getStatus());
    }
  }
}
