package ru.otus.restservice.config;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.reactivestreams.client.MongoClient;
import com.mongodb.reactivestreams.client.MongoClients;
import io.mongock.driver.mongodb.reactive.driver.MongoReactiveDriver;
import io.mongock.runner.springboot.MongockSpringboot;
import io.mongock.runner.springboot.base.MongockInitializingBeanRunner;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.mongodb.config.AbstractReactiveMongoConfiguration;
import org.springframework.data.mongodb.repository.config.EnableReactiveMongoRepositories;

import java.util.Collection;
import java.util.Collections;

@Configuration
@EnableConfigurationProperties({AppProps.class})
@EnableReactiveMongoRepositories(basePackages = "ru.otus")
public class ApplicationConfig extends AbstractReactiveMongoConfiguration {

    private final AppProps appProps;

    @Value("${spring.data.mongodb.uri:#{null}}")
    private String mongoUri;

    @Value("${spring.data.mongodb.database}")
    private String databaseName;

    public ApplicationConfig(AppProps appProps) {
        this.appProps = appProps;
    }

    @Override
    protected void configureClientSettings(MongoClientSettings.Builder builder) {
        String uri = appProps.mongodbConnectionString();
        if (uri == null) {
            uri = mongoUri;
        }
        ConnectionString connectionString = new ConnectionString(uri);
        builder.applyConnectionString(connectionString);
    }

    @Override
    protected String getDatabaseName() {
        return databaseName;
    }

    @Override
    public Collection<String> getMappingBasePackages() {
        return Collections.singleton("ru.otus");
    }

    @Override
    public boolean autoIndexCreation() {
        return true;
    }

    @Bean
    public MongoClient mongoClient() {
        return MongoClients.create();
    }

    @Bean
    @Profile({"production"})
    public MongockInitializingBeanRunner mongockInitializingBeanRunner(MongoClient reactiveMongoClient,
                                                                       ApplicationContext context) {
        return MongockSpringboot.builder()
                .setDriver(MongoReactiveDriver.withDefaultLock(reactiveMongoClient, getDatabaseName()))
                .addMigrationScanPackage("ru.otus.mongock.changelog")
                .setSpringContext(context)
                .setTransactionEnabled(false)
                .buildInitializingBeanRunner();
    }
}
