package com.waterworks.mlqs.etl.app.customers;

import com.waterworks.mlqs.etl.app.customers.domain.Customer;
import com.waterworks.mlqs.etl.infra.mongoin.dto.CustomerDocumentDTO;
import org.springframework.batch.item.ItemProcessor;

public class CustomerItemProcessor implements ItemProcessor<CustomerDocumentDTO, Customer> {
  @Override
  public Customer process(CustomerDocumentDTO item) throws Exception {
    return item.convertToCustomer();
  }
}
