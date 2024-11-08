package com.waterworks.mlqs.etl.infra.mongoin.dto;

import com.waterworks.mlqs.etl.app.employees.domain.Employee;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

@Document("hr_system_creation_employee")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class EmployeeDocumentDTO {
  private String employeeId;
  private String name;
  private String position;
  private String email;
  private String phoneNumber;
  private LocalDateTime creationDate;

  public Employee convertToEmployee() {
    return Employee.builder()
        .employeeId(this.employeeId)
        .name(this.name)
        .position(this.position)
        .email(this.email)
        .phoneNumber(this.phoneNumber)
        .creationDate(this.creationDate)
        .build();
  }
}
