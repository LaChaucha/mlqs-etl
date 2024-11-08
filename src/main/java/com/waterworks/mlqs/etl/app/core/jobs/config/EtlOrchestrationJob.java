package com.waterworks.mlqs.etl.app.core.jobs.config;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class EtlOrchestrationJob {
  @Bean
  public Job etlJob(JobRepository jobRepository,
                    Step extractTransformAndLoadEmployees,
                    Step extractTransformAndLoadCategories,
                    Step extractTransformAndLoadCustomers,
                    Step extractTransformAndLoadSuppliers,
                    Step extractTransformAndLoadProducts,
                    Step extractTransformAndLoadOrders,
                    Step extractTransformAndLoadOrderProduct,
                    Step extractTransformAndLoadInvoices,
                    Step extractTransformAndLoadTransaction) {
    return new JobBuilder("ETL - Datalake to datawarehouse", jobRepository)
        .incrementer(new RunIdIncrementer())
        .flow(extractTransformAndLoadEmployees)
        .next(extractTransformAndLoadCategories)
        .next(extractTransformAndLoadCustomers)
        .next(extractTransformAndLoadSuppliers)
        .next(extractTransformAndLoadProducts)
        .next(extractTransformAndLoadOrders)
        .next(extractTransformAndLoadOrderProduct)
        .next(extractTransformAndLoadInvoices)
        .next(extractTransformAndLoadTransaction)
        .end()
        .build();
  }
}
