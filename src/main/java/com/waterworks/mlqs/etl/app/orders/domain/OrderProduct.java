package com.waterworks.mlqs.etl.app.orders.domain;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class OrderProduct {
  private String orderId;
  private String productId;
  private LocalDateTime creationDate;
}
