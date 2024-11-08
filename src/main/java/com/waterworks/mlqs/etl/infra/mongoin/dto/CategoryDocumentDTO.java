package com.waterworks.mlqs.etl.infra.mongoin.dto;

import com.waterworks.mlqs.etl.app.categories.domain.Category;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

@Document("product_catalog_creation_category")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class CategoryDocumentDTO {
  private String categoryId;
  private String name;
  private LocalDateTime creationDate;

  public Category convertToCategory() {
    return Category.builder()
        .categoryId(this.categoryId)
        .name(this.name)
        .creationDate(this.creationDate)
        .build();
  }
}
