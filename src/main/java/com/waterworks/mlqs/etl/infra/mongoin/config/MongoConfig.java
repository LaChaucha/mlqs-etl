package com.waterworks.mlqs.etl.infra.mongoin.config;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoTemplate;

@Configuration
public class MongoConfig {

  @Value("${spring.data.mongodb.uri}")
  private String connection;

  @Value("${spring.data.mongodb.database}")
  private String databaseName;

  @Bean
  public MongoClient mongoClient() {
    ConnectionString connectionString = new ConnectionString(connection);
    MongoClientSettings mongoClientSettings = MongoClientSettings.builder()
        .applyConnectionString(connectionString)
        .build();

    return MongoClients.create(mongoClientSettings);
  }

  @Bean
  public MongoTemplate mongoTemplate() {
    return new MongoTemplate(mongoClient(), databaseName);
  }

}
