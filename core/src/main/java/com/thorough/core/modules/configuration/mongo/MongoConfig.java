package com.thorough.core.modules.configuration.mongo;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnProperty(name = "mongo.use", havingValue = "true")
public class MongoConfig {

    @Value("${mongo.url}")
    private String url;

    @Value("${mongo.databaseName}")
    private String databaseName ;

    @Bean
    public MongoClient mongoClient(){
        // 连接到 mongodb 服务
        MongoClient mongoClient = MongoClients.create(url);
        return mongoClient;
    }

    @Bean
    public MongoDatabase mongoDatabase(MongoClient mongoClient){
        // 连接到数据库
        MongoDatabase database = mongoClient.getDatabase(databaseName);
        return database;
    }
}
