package com.GoGoGoSearch.GoGoGo.config;


import com.GoGoGoSearch.GoGoGo.Repository.LinkRepo;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@EnableMongoRepositories(basePackageClasses = LinkRepo.class)
@Configuration
public class MongoDbConfig {
}
