package com.waterworks.mlqs.etl.app.invoices;

import com.waterworks.mlqs.etl.app.invoices.domain.Invoice;
import com.waterworks.mlqs.etl.infra.mongoin.dto.InvoiceDocumentDTO;
import org.springframework.batch.item.ItemProcessor;

public class InvoiceItemProcessor implements ItemProcessor<InvoiceDocumentDTO, Invoice> {
  @Override
  public Invoice process(InvoiceDocumentDTO item) throws Exception {
    return item.convertToInvoice();
  }
}
