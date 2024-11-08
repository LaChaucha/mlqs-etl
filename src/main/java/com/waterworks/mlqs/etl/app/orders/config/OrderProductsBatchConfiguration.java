package com.waterworks.mlqs.etl.app.orders.config;

import com.waterworks.mlqs.etl.app.orders.OrderProductItemProcessor;
import com.waterworks.mlqs.etl.app.orders.OrderProductsItemReader;
import com.waterworks.mlqs.etl.app.orders.domain.OrderProduct;
import java.util.List;
import javax.sql.DataSource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@Slf4j
public class OrderProductsBatchConfiguration {
  private static final int CHUNK_SIZE = 5000;

  @Bean
  @StepScope
  public ItemReader<OrderProduct> orderProductItemReader(final List<OrderProduct> orderProducts) {
    return new OrderProductsItemReader(orderProducts);
  }

  @Bean
  @StepScope
  public ItemProcessor<OrderProduct, OrderProduct> orderProductItemProcessor() {
    return new OrderProductItemProcessor();
  }

  @Bean
  public JdbcBatchItemWriter<OrderProduct> orderProductsWriter(
      @Qualifier("postgresDataSource") DataSource dataSource) {
    return new JdbcBatchItemWriterBuilder<OrderProduct>()
        .itemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<>())
        .assertUpdates(false)
        .sql("""
            INSERT INTO public.orderproducts (order_id, product_id)
            VALUES (:orderId, :productId);
            """)
        .dataSource(dataSource)
        .build();
  }

  @Bean
  public Step extractTransformAndLoadOrderProduct(final JobRepository jobRepository,
                                                  final PlatformTransactionManager transactionManager,
                                                  final ItemReader<OrderProduct> orderProductItemReader,
                                                  final ItemProcessor<OrderProduct, OrderProduct> orderProductItemProcessor,
                                                  final JdbcBatchItemWriter<OrderProduct> orderProductsWriter
  ) {
    return new StepBuilder("extractTransformAndLoadOrderProduct", jobRepository)
        .<OrderProduct, OrderProduct>chunk(CHUNK_SIZE, transactionManager)
        .reader(orderProductItemReader)
        .processor(orderProductItemProcessor)
        .writer(orderProductsWriter)
        .build();
  }
}
