package com.doce_ai.config;

import com.doce_ai.model.Documentation;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.index.Index;
import org.springframework.data.mongodb.core.index.IndexOperations;
import org.springframework.data.mongodb.core.index.IndexResolver;
import org.springframework.data.mongodb.core.index.MongoPersistentEntityIndexResolver;
import org.springframework.data.mongodb.core.mapping.MongoMappingContext;
import jakarta.annotation.PreDestroy;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

import java.util.concurrent.TimeUnit;

@Configuration
@EnableMongoRepositories(basePackages = "com.doce_ai.Repository")
public class MongoConfig {

    @Value("${spring.data.mongodb.uri}")
    private String mongoUri;

    @Value("${spring.data.mongodb.database}")
    private String database;

    // We'll store a reference to the created MongoClient
    private MongoClient mongoClient;

    @Bean
    public MongoClient mongoClient() {
        // Create the MongoClient using the URI from application.properties
        this.mongoClient = MongoClients.create(mongoUri);
        return this.mongoClient;
    }

    @Bean
    public MongoTemplate mongoTemplate() {
        return new MongoTemplate(mongoClient(), database); // Use database name here
    }

    @PreDestroy
    public void closeMongoClient() {
        // Ensure the MongoClient (and its monitoring threads) close cleanly
        if (this.mongoClient != null) {
            this.mongoClient.close();
        }
    }

    // Creates a Time-To-Live (TTL) index that:
    // Automatically deletes documents after 30 days (expire(30, TimeUnit.DAYS))
    // Uses createdDate field as expiration timestamp
    // Prevents database bloat from old documentation records
    // Automatically creates indexes from @Indexed annotations in entity classes
    // Ensures optimal query performance for frequent operations
    @Bean
    public IndexOperations indexOperations(MongoTemplate mongoTemplate, MongoMappingContext mongoMappingContext) {
        IndexOperations operations = mongoTemplate.indexOps(Documentation.class);
        IndexResolver resolver = new MongoPersistentEntityIndexResolver(mongoMappingContext);

        // Create TTL index for automatic document expiration after 30 days
        operations.ensureIndex(new Index().on("createdDate", Sort.Direction.ASC).expire(30, TimeUnit.DAYS));

        return operations;
    }
}
