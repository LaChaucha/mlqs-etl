package com.waterworks.mlqs.etl.app.core.jobs.config;


import java.util.HashMap;
import java.util.Map;
import org.springframework.batch.core.JobExecution;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CommonConfig {

  @Bean
  public Map<String, JobExecution> jobExecutionMap(){
    return new HashMap<String, JobExecution>();
  }

}
