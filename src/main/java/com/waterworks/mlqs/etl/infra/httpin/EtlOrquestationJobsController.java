package com.waterworks.mlqs.etl.infra.httpin;

import com.waterworks.mlqs.etl.app.core.services.GetJobStatusService;
import com.waterworks.mlqs.etl.app.core.services.JobService;
import java.util.concurrent.CompletableFuture;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/etl/")
@AllArgsConstructor
public class EtlOrquestationJobsController {

  private final JobService jobService;
  private final GetJobStatusService getJobStatusService;

  @PostMapping("jobs/{jobId}/start")
  public ResponseEntity<String> startJobs(@PathVariable final String jobId) {

    CompletableFuture.runAsync(() -> jobService.executeJob(jobId));

    return ResponseEntity.ok("Running...");
  }

  @GetMapping("jobs/{jobId}/sense")
  public ResponseEntity<String> senseJobs(@PathVariable final String jobId){
    return ResponseEntity.ok(getJobStatusService.getStatusByJobId(jobId));
  }
}
