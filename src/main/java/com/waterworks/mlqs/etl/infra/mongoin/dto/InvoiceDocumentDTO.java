package com.waterworks.mlqs.etl.infra.mongoin.dto;

import com.waterworks.mlqs.etl.app.invoices.domain.Invoice;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

@Document("payments_system_creation_invoice")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class InvoiceDocumentDTO {
  private String invoiceId;
  private String issueDate;
  private Double totalAmount;
  private String orderId;
  private LocalDateTime creationDate;

  public Invoice convertToInvoice() {
    return Invoice.builder()
        .invoiceId(this.invoiceId)
        .issueDate(this.issueDate)
        .totalAmount(this.totalAmount)
        .orderId(this.orderId)
        .creationDate(this.creationDate)
        .build();
  }
}
