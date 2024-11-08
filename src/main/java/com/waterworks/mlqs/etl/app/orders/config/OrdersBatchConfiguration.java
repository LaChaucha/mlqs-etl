package com.waterworks.mlqs.etl.app.orders.config;

import com.waterworks.mlqs.etl.app.orders.OrderItemProcessor;
import com.waterworks.mlqs.etl.app.orders.domain.Order;
import com.waterworks.mlqs.etl.app.orders.domain.OrderProduct;
import com.waterworks.mlqs.etl.infra.mongoin.dto.OrderDocumentDTO;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.sql.DataSource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.data.MongoItemReader;
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@Slf4j
public class OrdersBatchConfiguration {
  private static final int CHUNK_SIZE = 5000;

  @Bean
  @StepScope
  public MongoItemReader<OrderDocumentDTO> orderMongoItemReader(MongoTemplate mongoTemplate) {

    Instant now = Instant.now();

    Query query = new Query();
    query.addCriteria(Criteria.where("creationDate")
        .gte(now.minusSeconds(60 * 10 * 5)));

    Map<String, Sort.Direction> sortOptions = new HashMap<>();
    sortOptions.put("creationDate", Sort.Direction.ASC);

    MongoItemReader<OrderDocumentDTO> mongoItemReader = new MongoItemReader<>();

    //mongoItemReader.setCollection("hr_system_creation_employee");
    mongoItemReader.setQuery(query);
    mongoItemReader.setSort(sortOptions);
    mongoItemReader.setSaveState(false);
    mongoItemReader.setTargetType(OrderDocumentDTO.class);
    mongoItemReader.setTemplate(mongoTemplate);

    return mongoItemReader;
  }

  @Bean
  public List<OrderProduct> orderProducts(){
    return new ArrayList<>();
  }
  @Bean
  @StepScope
  public ItemProcessor<OrderDocumentDTO, Order> orderItemProcessor(
      final List<OrderProduct> orderProducts) {
    return new OrderItemProcessor(orderProducts);
  }

  @Bean
  public JdbcBatchItemWriter<Order> ordersWriter(
      @Qualifier("postgresDataSource") DataSource dataSource) {
    return new JdbcBatchItemWriterBuilder<Order>()
        .itemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<>())
        .assertUpdates(false)
        .sql("""
            INSERT INTO public.orderitem (order_id, order_date, status, payment_method, customer_id, seller_id)
            VALUES (:orderId, :orderDate, :status, :paymentMethod, :customerId, :sellerId)
            ON CONFLICT (order_id)
            DO UPDATE SET order_date = EXCLUDED.order_date,
                          status = EXCLUDED.status,
                          payment_method = EXCLUDED.payment_method,
                          customer_id = EXCLUDED.customer_id,
                          seller_id = EXCLUDED.seller_id;
            """)
        .dataSource(dataSource)
        .build();
  }

  @Bean
  public Step extractTransformAndLoadOrders(final JobRepository jobRepository,
                                               final PlatformTransactionManager transactionManager,
                                               final MongoItemReader<OrderDocumentDTO> mongoItemReader,
                                               final ItemProcessor<OrderDocumentDTO, Order> orderItemProcessor,
                                               final JdbcBatchItemWriter<Order> ordersWriter
  ) {
    return new StepBuilder("extractTransformAndLoadOrders", jobRepository)
        .<OrderDocumentDTO, Order>chunk(CHUNK_SIZE, transactionManager)
        .reader(mongoItemReader)
        .processor(orderItemProcessor)
        .writer(ordersWriter)
        .build();
  }
}
