package com.waterworks.mlqs.etl.app.employees.domain;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Employee {
  private String employeeId;
  private String name;
  private String position;
  private String email;
  private String phoneNumber;
  private LocalDateTime creationDate;
}
