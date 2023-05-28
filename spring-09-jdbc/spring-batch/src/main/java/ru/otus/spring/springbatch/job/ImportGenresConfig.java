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
import ru.otus.spring.springbatch.dto.GenreDto;

@Configuration
public class ImportGenresConfig extends AbstractMongoImport<GenreDto> {

    private static final String FLOW_NAME = "importGenresFlow";
    private static final String STEP_NAME = "importGenresStep";
    private static final String READER_NAME = "importGenresReader";
    private static final String SOURCE_COLLECTION_NAME = "genres";
    private static final String SOURCE_COLLECTION_QUERY = "{}";
    private static final String INSERT_SQL = "INSERT INTO public.genres(id, name) VALUES (:id, :name)";

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
    public Flow genresFlow(Step importGenresStep) {
        return getFlow(importGenresStep);
    }

    @Bean
    public Step importGenresStep(JobRepository jobRepository, PlatformTransactionManager transactionManager, MongoItemReader<GenreDto> genresReader, JdbcBatchItemWriter<GenreDto> genresWriter) {
        return getStep(STEP_NAME, jobRepository, transactionManager, genresReader, genresWriter);
    }

    @StepScope
    @Bean
    public MongoItemReader<GenreDto> genresReader(MongoTemplate mongoTemplate) {
        return getBuilder(mongoTemplate).targetType(GenreDto.class).build();
    }

    @StepScope
    @Bean
    public JdbcBatchItemWriter<GenreDto> genresWriter(JdbcBatchItemWriterBuilder<GenreDto> jdbcBatchItemWriterBuilder) {
        return getWriter(INSERT_SQL, jdbcBatchItemWriterBuilder);
    }
}
