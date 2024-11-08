package com.waterworks.mlqs.etl.app.invoices.config;

import com.waterworks.mlqs.etl.app.invoices.InvoiceItemProcessor;
import com.waterworks.mlqs.etl.app.invoices.domain.Invoice;
import com.waterworks.mlqs.etl.infra.mongoin.dto.InvoiceDocumentDTO;
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
public class InvoiceBatchConfiguration {
  private static final int CHUNK_SIZE = 5000;

  @Bean
  @StepScope
  public MongoItemReader<InvoiceDocumentDTO> invoiceMongoItemReader(MongoTemplate mongoTemplate) {

    Instant now = Instant.now();

    Query query = new Query();
    query.addCriteria(Criteria.where("creationDate")
        .gte(now.minusSeconds(60 * 10 * 5)));

    Map<String, Sort.Direction> sortOptions = new HashMap<>();
    sortOptions.put("creationDate", Sort.Direction.ASC);

    MongoItemReader<InvoiceDocumentDTO> mongoItemReader = new MongoItemReader<>();

    //mongoItemReader.setCollection("hr_system_creation_employee");
    mongoItemReader.setQuery(query);
    mongoItemReader.setSort(sortOptions);
    mongoItemReader.setSaveState(false);
    mongoItemReader.setTargetType(InvoiceDocumentDTO.class);
    mongoItemReader.setTemplate(mongoTemplate);

    return mongoItemReader;
  }

  @Bean
  @StepScope
  public ItemProcessor<InvoiceDocumentDTO, Invoice> invoiceItemProcessor() {
    return new InvoiceItemProcessor();
  }

  @Bean
  public JdbcBatchItemWriter<Invoice> invoiceWriter(
      @Qualifier("postgresDataSource") DataSource dataSource) {
    return new JdbcBatchItemWriterBuilder<Invoice>()
        .itemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<>())
        .assertUpdates(false)
        .sql("""
            INSERT INTO public.invoice (invoice_id, issue_date, total_amount, order_id)
            VALUES (:invoiceId, :issueDate, :totalAmount, :orderId)
            ON CONFLICT (invoice_id)
            DO UPDATE SET issue_date = EXCLUDED.issue_date,
                          total_amount = EXCLUDED.total_amount,
                          order_id = EXCLUDED.order_id;
            """)
        .dataSource(dataSource)
        .build();
  }

  @Bean
  public Step extractTransformAndLoadInvoices(final JobRepository jobRepository,
                                               final PlatformTransactionManager transactionManager,
                                               final MongoItemReader<InvoiceDocumentDTO> mongoItemReader,
                                               final ItemProcessor<InvoiceDocumentDTO, Invoice> invoiceItemProcessor,
                                               final JdbcBatchItemWriter<Invoice> invoiceWriter
  ) {
    return new StepBuilder("extractTransformAndLoadInvoices", jobRepository)
        .<InvoiceDocumentDTO, Invoice>chunk(CHUNK_SIZE, transactionManager)
        .reader(mongoItemReader)
        .processor(invoiceItemProcessor)
        .writer(invoiceWriter)
        .build();
  }
}
