package com.waterworks.mlqs.etl.app.products;

import com.waterworks.mlqs.etl.app.products.domain.Product;
import com.waterworks.mlqs.etl.infra.mongoin.dto.ProductDocumentDTO;
import org.springframework.batch.item.ItemProcessor;

public class ProductItemProcessor implements ItemProcessor<ProductDocumentDTO, Product> {
  @Override
  public Product process(ProductDocumentDTO item) throws Exception {
    return item.convertToProduct();
  }
}
