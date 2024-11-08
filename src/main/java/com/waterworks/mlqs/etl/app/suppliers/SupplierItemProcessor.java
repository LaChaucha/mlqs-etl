package com.waterworks.mlqs.etl.app.suppliers;

import com.waterworks.mlqs.etl.app.suppliers.domain.Supplier;
import com.waterworks.mlqs.etl.infra.mongoin.dto.SupplierDocumentDTO;
import org.springframework.batch.item.ItemProcessor;

public class SupplierItemProcessor implements ItemProcessor<SupplierDocumentDTO, Supplier> {
  @Override
  public Supplier process(SupplierDocumentDTO item) throws Exception {
    return item.convertToSupplier();
  }
}
