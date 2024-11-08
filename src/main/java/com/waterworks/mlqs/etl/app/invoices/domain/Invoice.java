package com.waterworks.mlqs.etl.app.invoices.domain;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Invoice {
  private String invoiceId;
  private String issueDate;
  private Double totalAmount;
  private String orderId;
  private LocalDateTime creationDate;
}
