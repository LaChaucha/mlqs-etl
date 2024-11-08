package com.waterworks.mlqs.etl.app.employees;

import com.waterworks.mlqs.etl.app.employees.domain.Employee;
import com.waterworks.mlqs.etl.infra.mongoin.dto.EmployeeDocumentDTO;
import org.springframework.batch.item.ItemProcessor;

public class EmployeeItemProcessor implements ItemProcessor<EmployeeDocumentDTO, Employee> {
  @Override
  public Employee process(EmployeeDocumentDTO item) throws Exception {
    return item.convertToEmployee();
  }
}
