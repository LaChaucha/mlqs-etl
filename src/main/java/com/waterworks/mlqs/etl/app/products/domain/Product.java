package com.waterworks.mlqs.etl.app.products.domain;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Product {
  private String productId;
  private String name;
  private String description;
  private Double price;
  private String categoryId;
  private String supplierId;
  private LocalDateTime creationDate;
}
