package com.waterworks.mlqs.etl.app.employees.config;

import com.waterworks.mlqs.etl.app.employees.domain.Employee;
import com.waterworks.mlqs.etl.app.employees.EmployeeItemProcessor;
import com.waterworks.mlqs.etl.infra.mongoin.dto.EmployeeDocumentDTO;
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
public class EmployeesBatchConfiguration {
  private static final int CHUNK_SIZE = 5000;

  @Bean
  @StepScope
  public MongoItemReader<EmployeeDocumentDTO> employeeMongoItemReader(MongoTemplate mongoTemplate) {

    Instant now = Instant.now();

    Query query = new Query();
    query.addCriteria(Criteria.where("creationDate")
        .gte(now.minusSeconds(60 * 10 * 5)));

    Map<String, Sort.Direction> sortOptions = new HashMap<>();
    sortOptions.put("creationDate", Sort.Direction.ASC);

    MongoItemReader<EmployeeDocumentDTO> mongoItemReader = new MongoItemReader<>();

    //mongoItemReader.setCollection("hr_system_creation_employee");
    mongoItemReader.setQuery(query);
    mongoItemReader.setSort(sortOptions);
    mongoItemReader.setSaveState(false);
    mongoItemReader.setTargetType(EmployeeDocumentDTO.class);
    mongoItemReader.setTemplate(mongoTemplate);

    return mongoItemReader;
  }

  @Bean
  @StepScope
  public ItemProcessor<EmployeeDocumentDTO, Employee> employeeItemProcessor() {
    return new EmployeeItemProcessor();
  }

  @Bean
  public JdbcBatchItemWriter<Employee> employeeWriter(
      @Qualifier("postgresDataSource") DataSource dataSource) {
    return new JdbcBatchItemWriterBuilder<Employee>()
        .itemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<>())
        .assertUpdates(false)
        .sql("""
            INSERT INTO employee (employee_id, "name", "position", email, phone_number)
            VALUES (:employeeId, :name, :position, :email, :phoneNumber)
            ON CONFLICT (employee_id)
            DO UPDATE SET name = EXCLUDED.name,
                          "position" = EXCLUDED."position",
                          email = EXCLUDED.email,
                          phone_number = EXCLUDED.phone_number
            """)
        .dataSource(dataSource)
        .build();
  }

  @Bean
  public Step extractTransformAndLoadEmployees(final JobRepository jobRepository,
                                               final PlatformTransactionManager transactionManager,
                                               final MongoItemReader<EmployeeDocumentDTO> mongoItemReader,
                                               final ItemProcessor<EmployeeDocumentDTO, Employee> employeeItemProcessor,
                                               final JdbcBatchItemWriter<Employee> employeeWriter
  ) {
    return new StepBuilder("extractTransformAndLoadEmployees", jobRepository)
        .<EmployeeDocumentDTO, Employee>chunk(CHUNK_SIZE, transactionManager)
        .reader(mongoItemReader)
        .processor(employeeItemProcessor)
        .writer(employeeWriter)
        .build();
  }
}
