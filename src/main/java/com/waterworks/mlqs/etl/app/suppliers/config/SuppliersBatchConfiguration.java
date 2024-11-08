package com.waterworks.mlqs.etl.app.suppliers.config;

import com.waterworks.mlqs.etl.app.suppliers.SupplierItemProcessor;
import com.waterworks.mlqs.etl.app.suppliers.domain.Supplier;
import com.waterworks.mlqs.etl.infra.mongoin.dto.SupplierDocumentDTO;
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
public class SuppliersBatchConfiguration {
  private static final int CHUNK_SIZE = 5000;

  @Bean
  @StepScope
  public MongoItemReader<SupplierDocumentDTO> supplierMongoItemReader(MongoTemplate mongoTemplate) {

    Instant now = Instant.now();

    Query query = new Query();
    query.addCriteria(Criteria.where("creationDate")
        .gte(now.minusSeconds(60 * 10 * 5)));

    Map<String, Sort.Direction> sortOptions = new HashMap<>();
    sortOptions.put("creationDate", Sort.Direction.ASC);

    MongoItemReader<SupplierDocumentDTO> mongoItemReader = new MongoItemReader<>();

    //mongoItemReader.setCollection("hr_system_creation_employee");
    mongoItemReader.setQuery(query);
    mongoItemReader.setSort(sortOptions);
    mongoItemReader.setSaveState(false);
    mongoItemReader.setTargetType(SupplierDocumentDTO.class);
    mongoItemReader.setTemplate(mongoTemplate);

    return mongoItemReader;
  }

  @Bean
  @StepScope
  public ItemProcessor<SupplierDocumentDTO, Supplier> supplierItemProcessor() {
    return new SupplierItemProcessor();
  }

  @Bean
  public JdbcBatchItemWriter<Supplier> supplierWriter(
      @Qualifier("postgresDataSource") DataSource dataSource) {
    return new JdbcBatchItemWriterBuilder<Supplier>()
        .itemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<>())
        .assertUpdates(false)
        .sql("""
            INSERT INTO public.supplier (supplier_id, "name", address, email, phone_number)
            VALUES (:supplierId, :name, :address, :email, :phoneNumber)
            ON CONFLICT (supplier_id)
            DO UPDATE SET "name" = EXCLUDED."name",
                          address = EXCLUDED.address,
                          email = EXCLUDED.email,
                          phone_number = EXCLUDED.phone_number
            """)
        .dataSource(dataSource)
        .build();
  }

  @Bean
  public Step extractTransformAndLoadSuppliers(final JobRepository jobRepository,
                                               final PlatformTransactionManager transactionManager,
                                               final MongoItemReader<SupplierDocumentDTO> mongoItemReader,
                                               final ItemProcessor<SupplierDocumentDTO, Supplier> supplierItemProcessor,
                                               final JdbcBatchItemWriter<Supplier> supplierWriter
  ) {
    return new StepBuilder("extractTransformAndLoadSupplier", jobRepository)
        .<SupplierDocumentDTO, Supplier>chunk(CHUNK_SIZE, transactionManager)
        .reader(mongoItemReader)
        .processor(supplierItemProcessor)
        .writer(supplierWriter)
        .build();
  }
}
