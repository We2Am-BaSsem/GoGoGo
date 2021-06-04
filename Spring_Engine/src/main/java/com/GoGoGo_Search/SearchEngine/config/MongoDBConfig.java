package com.GoGoGo_Search.SearchEngine.config;


import com.GoGoGo_Search.SearchEngine.LinkRepository.LinkRepo;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@EnableMongoRepositories(basePackageClasses = LinkRepo.class)
@Configuration
public class MongoDBConfig {


}
