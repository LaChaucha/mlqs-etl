package com.waterworks.mlqs.etl.app.products.config;

import com.waterworks.mlqs.etl.app.products.ProductItemProcessor;
import com.waterworks.mlqs.etl.app.products.domain.Product;
import com.waterworks.mlqs.etl.infra.mongoin.dto.ProductDocumentDTO;
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
public class ProductsBatchConfiguration {
  private static final int CHUNK_SIZE = 5000;

  @Bean
  @StepScope
  public MongoItemReader<ProductDocumentDTO> productMongoItemReader(MongoTemplate mongoTemplate) {

    Instant now = Instant.now();

    Query query = new Query();
    query.addCriteria(Criteria.where("creationDate")
        .gte(now.minusSeconds(60 * 10 * 5)));

    Map<String, Sort.Direction> sortOptions = new HashMap<>();
    sortOptions.put("creationDate", Sort.Direction.ASC);

    MongoItemReader<ProductDocumentDTO> mongoItemReader = new MongoItemReader<>();

    //mongoItemReader.setCollection("hr_system_creation_employee");
    mongoItemReader.setQuery(query);
    mongoItemReader.setSort(sortOptions);
    mongoItemReader.setSaveState(false);
    mongoItemReader.setTargetType(ProductDocumentDTO.class);
    mongoItemReader.setTemplate(mongoTemplate);

    return mongoItemReader;
  }

  @Bean
  @StepScope
  public ItemProcessor<ProductDocumentDTO, Product> productItemProcessor() {
    return new ProductItemProcessor();
  }

  @Bean
  public JdbcBatchItemWriter<Product> productWriter(
      @Qualifier("postgresDataSource") DataSource dataSource) {
    return new JdbcBatchItemWriterBuilder<Product>()
        .itemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<>())
        .assertUpdates(false)
        .sql("""
            INSERT INTO public.product (product_id, "name", description, price, category_id, supplier_id)
            VALUES (:productId, :name, :description, :price, :categoryId, :supplierId)
            ON CONFLICT (product_id)
            DO UPDATE SET "name" = EXCLUDED."name",
                          description = EXCLUDED.description,
                          price = EXCLUDED.price,
                          category_id = EXCLUDED.category_id,
                          supplier_id = EXCLUDED.supplier_id
            """)
        .dataSource(dataSource)
        .build();
  }

  @Bean
  public Step extractTransformAndLoadProducts(final JobRepository jobRepository,
                                               final PlatformTransactionManager transactionManager,
                                               final MongoItemReader<ProductDocumentDTO> mongoItemReader,
                                               final ItemProcessor<ProductDocumentDTO, Product> productItemProcessor,
                                               final JdbcBatchItemWriter<Product> productWriter
  ) {
    return new StepBuilder("extractTransformAndLoadProducts", jobRepository)
        .<ProductDocumentDTO, Product>chunk(CHUNK_SIZE, transactionManager)
        .reader(mongoItemReader)
        .processor(productItemProcessor)
        .writer(productWriter)
        .build();
  }
}
