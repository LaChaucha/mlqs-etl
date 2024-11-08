package com.waterworks.mlqs.etl.app.suppliers.domain;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Supplier {
  private String supplierId;
  private String name;
  private String address;
  private String email;
  private String phoneNumber;
  private LocalDateTime creationDate;
}
