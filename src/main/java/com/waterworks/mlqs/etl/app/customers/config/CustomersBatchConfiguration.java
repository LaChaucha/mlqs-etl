package com.waterworks.mlqs.etl.app.customers.config;

import com.waterworks.mlqs.etl.app.customers.CustomerItemProcessor;
import com.waterworks.mlqs.etl.app.customers.domain.Customer;
import com.waterworks.mlqs.etl.infra.mongoin.dto.CustomerDocumentDTO;
import java.time.Instant;
import java.util.HashMap;
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
public class CustomersBatchConfiguration {
  private static final int CHUNK_SIZE = 5000;

  @Bean
  @StepScope
  public MongoItemReader<CustomerDocumentDTO> customerMongoItemReader(
      MongoTemplate mongoTemplate) {

    Instant now = Instant.now();

    Query query = new Query();
    query.addCriteria(Criteria.where("creationDate")
        .gte(now.minusSeconds(60 * 10 * 5)));

    Map<String, Sort.Direction> sortOptions = new HashMap<>();
    sortOptions.put("creationDate", Sort.Direction.ASC);

    MongoItemReader<CustomerDocumentDTO> mongoItemReader = new MongoItemReader<>();

    mongoItemReader.setCollection("product_catalog_creation_customer");
    mongoItemReader.setQuery(query);
    mongoItemReader.setSort(sortOptions);
    mongoItemReader.setSaveState(false);
    mongoItemReader.setTargetType(CustomerDocumentDTO.class);
    mongoItemReader.setTemplate(mongoTemplate);

    return mongoItemReader;
  }

  @Bean
  @StepScope
  public ItemProcessor<CustomerDocumentDTO, Customer> customerItemProcessor() {
    return new CustomerItemProcessor();
  }

  @Bean
  public JdbcBatchItemWriter<Customer> customerWriter(
      @Qualifier("postgresDataSource") DataSource dataSource) {
    return new JdbcBatchItemWriterBuilder<Customer>()
        .itemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<>())
        .assertUpdates(false)
        .sql("""
            INSERT INTO public.customer (customer_id, "name", address, email, phone_number)
            VALUES (:customerId, :name, :address, :email, :phoneNumber)
            ON CONFLICT (customer_id)
            DO UPDATE SET "name" = EXCLUDED."name",
                          address = EXCLUDED.address,
                          email = EXCLUDED.email,
                          phone_number = EXCLUDED.phone_number
              """)
        .dataSource(dataSource)
        .build();
  }

  @Bean
  public Step extractTransformAndLoadCustomers(final JobRepository jobRepository,
                                               final PlatformTransactionManager transactionManager,
                                               final MongoItemReader<CustomerDocumentDTO> customerMongoItemReader,
                                               final ItemProcessor<CustomerDocumentDTO, Customer> customerItemProcessor,
                                               final JdbcBatchItemWriter<Customer> customerWriter
  ) {
    return new StepBuilder("extractTransformAndLoadCustomers", jobRepository)
        .<CustomerDocumentDTO, Customer>chunk(CHUNK_SIZE, transactionManager)
        .reader(customerMongoItemReader)
        .processor(customerItemProcessor)
        .writer(customerWriter)
        .build();
  }
}
