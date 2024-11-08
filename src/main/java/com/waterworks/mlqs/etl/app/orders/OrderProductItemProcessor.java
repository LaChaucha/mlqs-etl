package com.waterworks.mlqs.etl.app.orders;

import com.waterworks.mlqs.etl.app.orders.domain.OrderProduct;
import org.springframework.batch.item.ItemProcessor;

public class OrderProductItemProcessor implements ItemProcessor<OrderProduct, OrderProduct> {

  @Override
  public OrderProduct process(OrderProduct item) throws Exception {
    return item;
  }
}
