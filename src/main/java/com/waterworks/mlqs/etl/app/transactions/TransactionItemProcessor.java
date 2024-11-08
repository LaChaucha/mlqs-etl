package com.waterworks.mlqs.etl.app.transactions;

import com.waterworks.mlqs.etl.app.transactions.domain.Transaction;
import com.waterworks.mlqs.etl.infra.mongoin.dto.TransactionDocumentDTO;
import org.springframework.batch.item.ItemProcessor;

public class TransactionItemProcessor implements ItemProcessor<TransactionDocumentDTO, Transaction> {
  @Override
  public Transaction process(TransactionDocumentDTO item) throws Exception {
    return item.convertToTransaction();
  }
}
