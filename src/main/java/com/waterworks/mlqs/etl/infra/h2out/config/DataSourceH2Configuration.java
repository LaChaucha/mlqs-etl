package com.waterworks.mlqs.etl.infra.h2out.config;

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
public class DataSourceH2Configuration {
  @Value("${h2.datasource.driver}")
  private String h2DataSourceDriver;

  @Value("${h2.datasource.url}")
  private String h2DataSourceUrl;

  @Value("${h2.datasource.user}")
  private String h2DataSourceUser;

  @Value("${h2.datasource.password}")
  private String h2DataSourcePassword;

  @Primary
  @Bean(name = "h2DataSource")
  public DataSource h2DataSource() {
    DriverManagerDataSource dataSource = new DriverManagerDataSource();
    dataSource.setDriverClassName(h2DataSourceDriver);
    dataSource.setUrl(h2DataSourceUrl);
    dataSource.setUsername(h2DataSourceUser);
    dataSource.setPassword(h2DataSourcePassword);
    return dataSource;
  }

  @Bean
  public DataSourceInitializer h2DataSourceInitializer(@Qualifier("h2DataSource") DataSource dataSource) {
    DataSourceInitializer initializer = new DataSourceInitializer();
    initializer.setDataSource(dataSource);
    return initializer;
  }

  @Bean(name = "h2JdbcTemplate")
  public JdbcTemplate h2JdbcTemplate(@Qualifier("h2DataSource") DataSource dataSource) {
    return new JdbcTemplate(dataSource);
  }
}
