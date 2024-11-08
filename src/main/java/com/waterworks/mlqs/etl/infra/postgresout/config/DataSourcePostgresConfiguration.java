package com.waterworks.mlqs.etl.infra.postgresout.config;

import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.jdbc.datasource.init.DataSourceInitializer;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;

@Configuration
public class DataSourcePostgresConfiguration {



  @Value("${postgres.datasource.driver}")
  private String postgresDataSourceDriver;

  @Value("${postgres.datasource.url}")
  private String postgresDataSourceUrl;

  @Value("${postgres.datasource.user}")
  private String postgresDataSourceUser;

  @Value("${postgres.datasource.password}")
  private String postgresDataSourcePassword;

  @Bean(name = "postgresDataSource")
  public DataSource postgresDataSource() {
    DriverManagerDataSource dataSource = new DriverManagerDataSource();
    dataSource.setDriverClassName(postgresDataSourceDriver);
    dataSource.setUrl(postgresDataSourceUrl);
    dataSource.setUsername(postgresDataSourceUser);
    dataSource.setPassword(postgresDataSourcePassword);
    return dataSource;
  }

  @Bean
  public DataSourceInitializer postgresDataSourceInitializer(@Qualifier("postgresDataSource") DataSource dataSource) {
    DataSourceInitializer initializer = new DataSourceInitializer();
    initializer.setDataSource(dataSource);
    return initializer;
  }

  @Bean(name = "postgresJdbcTemplate")
  public JdbcTemplate postgresJdbcTemplate(@Qualifier("postgresDataSource") DataSource dataSource) {
    return new JdbcTemplate(dataSource);
  }
}