package com.waterworks.mlqs.etl.app.orders;

import com.waterworks.mlqs.etl.app.orders.domain.Order;
import com.waterworks.mlqs.etl.app.orders.domain.OrderProduct;
import com.waterworks.mlqs.etl.infra.mongoin.dto.OrderDocumentDTO;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.batch.item.ItemProcessor;

@AllArgsConstructor
public class OrderItemProcessor implements ItemProcessor<OrderDocumentDTO, Order> {
  private final List<OrderProduct> orderProducts;

  @Override
  public Order process(OrderDocumentDTO item) throws Exception {
    final Order order = item.convertToOrder();
    order.getProducts()
        .forEach(product -> orderProducts.add(OrderProduct.builder()
            .productId(product.getProductId())
            .orderId(order.getOrderId())
            .creationDate(order.getCreationDate())
            .build()));
    return order;
  }
}
