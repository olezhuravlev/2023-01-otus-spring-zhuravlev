package ru.otus.spring.springbatch.job;

import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.flow.Flow;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.item.data.MongoItemReader;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.transaction.PlatformTransactionManager;
import ru.otus.spring.springbatch.dto.AuthorDto;

@Configuration
public class ImportAuthorsConfig extends AbstractMongoImport<AuthorDto> {

    private static final String FLOW_NAME = "importAuthorsFlow";
    private static final String STEP_NAME = "importAuthorsStep";
    private static final String READER_NAME = "importAuthorsReader";
    private static final String SOURCE_COLLECTION_NAME = "authors";
    private static final String SOURCE_COLLECTION_QUERY = "{}";
    private static final String INSERT_SQL = "INSERT INTO public.authors(id, name) VALUES (:id, :name)";

    @Override
    protected String getFlowName() {
        return FLOW_NAME;
    }

    @Override
    protected String getReaderName() {
        return READER_NAME;
    }

    @Override
    protected String getSourceName() {
        return SOURCE_COLLECTION_NAME;
    }

    @Override
    protected String getSourceQuery() {
        return SOURCE_COLLECTION_QUERY;
    }

    @Bean
    public Flow authorsFlow(Step importAuthorsStep) {
        return getFlow(importAuthorsStep);
    }

    @Bean
    public Step importAuthorsStep(JobRepository jobRepository, PlatformTransactionManager transactionManager, MongoItemReader<AuthorDto> authorsReader, JdbcBatchItemWriter<AuthorDto> authorsWriter) {
        return getStep(STEP_NAME, jobRepository, transactionManager, authorsReader, authorsWriter);
    }

    @StepScope
    @Bean
    public MongoItemReader<AuthorDto> authorsReader(MongoTemplate mongoTemplate) {
        return getBuilder(mongoTemplate).targetType(AuthorDto.class).build();
    }

    @StepScope
    @Bean
    public JdbcBatchItemWriter<AuthorDto> authorsWriter(JdbcBatchItemWriterBuilder<AuthorDto> jdbcBatchItemWriterBuilder) {
        return getWriter(INSERT_SQL, jdbcBatchItemWriterBuilder);
    }
}
