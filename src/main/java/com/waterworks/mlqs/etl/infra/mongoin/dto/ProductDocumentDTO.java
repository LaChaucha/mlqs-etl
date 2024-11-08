package com.waterworks.mlqs.etl.infra.mongoin.dto;

import com.waterworks.mlqs.etl.app.products.domain.Product;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

@Document("product_catalog_creation_product")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class ProductDocumentDTO {
  private String productId;
  private String name;
  private String description;
  private Double price;
  private String categoryId;
  private String supplierId;
  private LocalDateTime creationDate;

  public Product convertToProduct() {
    return Product.builder()
        .productId(this.productId)
        .name(this.name)
        .description(this.description)
        .price(this.price)
        .categoryId(this.categoryId)
        .supplierId(this.supplierId)
        .creationDate(this.creationDate)
        .build();
  }
}
