package com.waterworks.mlqs.etl.infra.mongoin.dto;

import com.waterworks.mlqs.etl.app.customers.domain.Customer;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

@Document("product_catalog_creation_category")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class CustomerDocumentDTO {
  private String customerId;
  private String name;
  private String address;
  private String email;
  private String phoneNumber;
  private LocalDateTime creationDate;

  public Customer convertToCustomer() {
    return Customer.builder()
        .customerId(this.customerId)
        .name(this.name)
        .address(this.address)
        .email(this.email)
        .phoneNumber(this.phoneNumber)
        .creationDate(this.creationDate)
        .build();
  }
}
