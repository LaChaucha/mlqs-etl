package com.waterworks.mlqs.etl.infra.mongoin.dto;

import com.waterworks.mlqs.etl.app.orders.domain.Order;
import com.waterworks.mlqs.etl.app.orders.domain.OrderProduct;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

@Document("payments_system_creation_orderitem")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class OrderDocumentDTO {
  private String orderId;
  private String orderDate;
  private String status;
  private String paymentMethod;
  private String customerId;
  private String sellerId;
  private List<ProductDocumentDTO> products;
  private LocalDateTime creationDate;

  public Order convertToOrder() {
    return Order.builder()
        .orderId(this.orderId)
        .orderDate(this.orderDate)
        .status(this.status)
        .paymentMethod(this.paymentMethod)
        .customerId(this.customerId)
        .sellerId(this.sellerId)
        .products(Objects.nonNull(this.products)
            ? this.products.stream().map(ProductDocumentDTO::convertToProduct).toList()
            : null)
        .creationDate(this.creationDate)
        .build();
  }

  public List<OrderProduct> convertToOrderProduct() {
    return products.stream()
        .map(productDocumentDTO -> OrderProduct.builder()
            .orderId(this.orderId)
            .productId(productDocumentDTO.getProductId())
            .creationDate(this.creationDate)
            .build())
        .toList();
  }
}
