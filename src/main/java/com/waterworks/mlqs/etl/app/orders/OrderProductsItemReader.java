package com.waterworks.mlqs.etl.app.orders;

import com.waterworks.mlqs.etl.app.orders.domain.OrderProduct;
import java.util.Iterator;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.NonTransientResourceException;
import org.springframework.batch.item.ParseException;
import org.springframework.batch.item.UnexpectedInputException;

@AllArgsConstructor
public class OrderProductsItemReader implements ItemReader<OrderProduct> {
  private final List<OrderProduct> orderProducts;
  private Iterator<OrderProduct> iterator;

  public OrderProductsItemReader(final List<OrderProduct> orderProducts){
    this.orderProducts = orderProducts;
    this.iterator = orderProducts.iterator();
  }
  @Override
  public OrderProduct read()
      throws Exception, UnexpectedInputException, ParseException, NonTransientResourceException {

    if (this.iterator.hasNext()){
      return this.iterator.next();
    }
    this.orderProducts.clear();
    this.iterator = this.orderProducts.iterator();
    return null;
  }
}
