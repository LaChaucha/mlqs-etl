package com.waterworks.mlqs.etl.app.categories.domain;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Category {
  private String categoryId;
  private String name;
  private LocalDateTime creationDate;
}
