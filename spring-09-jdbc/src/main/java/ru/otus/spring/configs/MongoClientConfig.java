package ru.otus.spring.configs;

import com.github.cloudyrock.spring.v5.EnableMongock;
import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.AbstractMongoClientConfiguration;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import ru.otus.spring.listeners.BookEventListener;

import java.util.Collection;
import java.util.Collections;

@Configuration
@EnableMongock
@EnableMongoRepositories(basePackages = "ru.otus.spring.repositories")
public class MongoClientConfig extends AbstractMongoClientConfiguration {

    @Override
    protected String getDatabaseName() {
        return "librarydb";
    }

    @Override
    public boolean autoIndexCreation() {
        return true;
    }

    @Override
    protected void configureClientSettings(MongoClientSettings.Builder builder) {
        ConnectionString connectionString = new ConnectionString("mongodb://mongodb:27017");
        builder.applyConnectionString(connectionString);
    }

    @Override
    public Collection<String> getMappingBasePackages() {
        return Collections.singleton("ru.otus.spring");
    }

    @Bean
    public BookEventListener bookEventListener() {
        return new BookEventListener();
    }
}
