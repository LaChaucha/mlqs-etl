package com.waterworks.mlqs.etl.app.transactions.config;

import com.waterworks.mlqs.etl.app.transactions.TransactionItemProcessor;
import com.waterworks.mlqs.etl.app.transactions.domain.Transaction;
import com.waterworks.mlqs.etl.infra.mongoin.dto.TransactionDocumentDTO;
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
public class TransactionsBatchConfiguration {
  private static final int CHUNK_SIZE = 5000;

  @Bean
  @StepScope
  public MongoItemReader<TransactionDocumentDTO> transactionMongoItemReader(
      MongoTemplate mongoTemplate) {

    Instant now = Instant.now();

    Query query = new Query();
    query.addCriteria(Criteria.where("creationDate")
        .gte(now.minusSeconds(60 * 10 * 5)));

    Map<String, Sort.Direction> sortOptions = new HashMap<>();
    sortOptions.put("creationDate", Sort.Direction.ASC);

    MongoItemReader<TransactionDocumentDTO> mongoItemReader = new MongoItemReader<>();

    //mongoItemReader.setCollection("hr_system_creation_employee");
    mongoItemReader.setQuery(query);
    mongoItemReader.setSort(sortOptions);
    mongoItemReader.setSaveState(false);
    mongoItemReader.setTargetType(TransactionDocumentDTO.class);
    mongoItemReader.setTemplate(mongoTemplate);

    return mongoItemReader;
  }

  @Bean
  @StepScope
  public ItemProcessor<TransactionDocumentDTO, Transaction> transactionItemProcessor() {
    return new TransactionItemProcessor();
  }

  @Bean
  public JdbcBatchItemWriter<Transaction> transactionWriter(
      @Qualifier("postgresDataSource") DataSource dataSource) {
    return new JdbcBatchItemWriterBuilder<Transaction>()
        .itemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<>())
        .assertUpdates(false)
        .sql("""
            INSERT INTO public.transaction (transaction_id, transaction_type, amount, transaction_date, invoice_id)
            VALUES (:transactionId, :transactionType, :amount, :transactionDate, :invoiceId)
            ON CONFLICT (transaction_id)
            DO UPDATE SET transaction_type = EXCLUDED.transaction_type,
                          amount = EXCLUDED.amount,
                          transaction_date = EXCLUDED.transaction_date,
                          invoice_id = EXCLUDED.invoice_id;
            """)
        .dataSource(dataSource)
        .build();
  }

  @Bean
  public Step extractTransformAndLoadTransaction(final JobRepository jobRepository,
                                                 final PlatformTransactionManager transactionManager,
                                                 final MongoItemReader<TransactionDocumentDTO> mongoItemReader,
                                                 final ItemProcessor<TransactionDocumentDTO, Transaction> transactionItemProcessor,
                                                 final JdbcBatchItemWriter<Transaction> transactionWriter
  ) {
    return new StepBuilder("extractTransformAndLoadTransaction", jobRepository)
        .<TransactionDocumentDTO, Transaction>chunk(CHUNK_SIZE, transactionManager)
        .reader(mongoItemReader)
        .processor(transactionItemProcessor)
        .writer(transactionWriter)
        .build();
  }
}
