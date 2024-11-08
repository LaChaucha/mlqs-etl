package com.waterworks.mlqs.etl;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;


@SpringBootApplication
@EnableScheduling
public class MlqsEtl {

  /**
   * The main method is the entry point for the application. It initializes and starts the Spring
   * Boot application.
   *
   * @param args Command-line arguments that can be passed when launching the application.
   */
  public static void main(String[] args) {
    SpringApplication.run(MlqsEtl.class, args);
  }
}
