package com.waterworks.mlqs.etl.app.categories;

import com.waterworks.mlqs.etl.app.categories.domain.Category;
import com.waterworks.mlqs.etl.infra.mongoin.dto.CategoryDocumentDTO;
import org.springframework.batch.item.ItemProcessor;

public class CategoryItemProcessor implements ItemProcessor<CategoryDocumentDTO, Category> {
  @Override
  public Category process(CategoryDocumentDTO item) throws Exception {
    return item.convertToCategory();
  }
}
