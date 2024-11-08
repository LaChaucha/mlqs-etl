package com.waterworks.mlqs.etl.app.transactions.domain;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Transaction {
  private String transactionId;
  private String transactionType;
  private Double amount;
  private String transactionDate;
  private String invoiceId;
  private LocalDateTime creationDate;
}
