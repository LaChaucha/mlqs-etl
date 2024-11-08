package com.waterworks.mlqs.etl.infra.mongoin.dto;

import com.waterworks.mlqs.etl.app.transactions.domain.Transaction;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

@Document("payments_system_creation_transaction")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class TransactionDocumentDTO {
  private String transactionId;
  private String transactionType;
  private Double amount;
  private String transactionDate;
  private String invoiceId;
  private LocalDateTime creationDate;

  public Transaction convertToTransaction() {
    return Transaction.builder()
        .transactionId(this.transactionId)
        .transactionType(this.transactionType)
        .amount(this.amount)
        .transactionDate(this.transactionDate)
        .invoiceId(this.invoiceId)
        .creationDate(this.creationDate)
        .build();
  }
}
