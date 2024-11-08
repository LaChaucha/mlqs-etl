package com.waterworks.mlqs.etl.app.categories.config;

import com.waterworks.mlqs.etl.app.categories.CategoryItemProcessor;
import com.waterworks.mlqs.etl.app.categories.domain.Category;
import com.waterworks.mlqs.etl.infra.mongoin.dto.CategoryDocumentDTO;
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
public class CategoriesBatchConfiguration {
  private static final int CHUNK_SIZE = 5000;

  @Bean
  @StepScope
  public MongoItemReader<CategoryDocumentDTO> categoryMongoItemReader(
      MongoTemplate mongoTemplate) {

    Instant now = Instant.now();

    Query query = new Query();
    query.addCriteria(Criteria.where("creationDate")
        .gte(now.minusSeconds(60 * 10 * 5)));

    Map<String, Sort.Direction> sortOptions = new HashMap<>();
    sortOptions.put("creationDate", Sort.Direction.ASC);

    MongoItemReader<CategoryDocumentDTO> mongoItemReader = new MongoItemReader<>();

    mongoItemReader.setCollection("product_catalog_creation_category");
    mongoItemReader.setQuery(query);
    mongoItemReader.setSort(sortOptions);
    mongoItemReader.setSaveState(false);
    mongoItemReader.setTargetType(CategoryDocumentDTO.class);
    mongoItemReader.setTemplate(mongoTemplate);

    return mongoItemReader;
  }

  @Bean
  @StepScope
  public ItemProcessor<CategoryDocumentDTO, Category> categoryItemProcessor() {
    return new CategoryItemProcessor();
  }

  @Bean
  public JdbcBatchItemWriter<Category> categoryWriter(
      @Qualifier("postgresDataSource") DataSource dataSource) {
    return new JdbcBatchItemWriterBuilder<Category>()
        .itemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<>())
        .assertUpdates(false)
        .sql("""
            INSERT INTO public.category (category_id, "name")
            VALUES (:categoryId, :name)
            ON CONFLICT (category_id)
            DO UPDATE SET "name" = EXCLUDED."name"
              """)
        .dataSource(dataSource)
        .build();
  }

  @Bean
  public Step extractTransformAndLoadCategories(final JobRepository jobRepository,
                                               final PlatformTransactionManager transactionManager,
                                               final MongoItemReader<CategoryDocumentDTO> categoryMongoItemReader,
                                               final ItemProcessor<CategoryDocumentDTO, Category> categoryItemProcessor,
                                               final JdbcBatchItemWriter<Category> categoryWriter
  ) {
    return new StepBuilder("extractTransformAndLoadEmployees", jobRepository)
        .<CategoryDocumentDTO, Category>chunk(CHUNK_SIZE, transactionManager)
        .reader(categoryMongoItemReader)
        .processor(categoryItemProcessor)
        .writer(categoryWriter)
        .build();
  }
}
