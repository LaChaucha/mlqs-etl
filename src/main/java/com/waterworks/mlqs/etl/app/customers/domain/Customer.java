package com.waterworks.mlqs.etl.app.customers.domain;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Customer {
  private String customerId;
  private String name;
  private String address;
  private String email;
  private String phoneNumber;
  private LocalDateTime creationDate;
}
