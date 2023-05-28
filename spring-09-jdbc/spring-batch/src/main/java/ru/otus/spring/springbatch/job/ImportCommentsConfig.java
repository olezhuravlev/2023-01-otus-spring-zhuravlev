package ru.otus.spring.springbatch.job;

import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.item.data.MongoItemReader;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.transaction.PlatformTransactionManager;
import ru.otus.spring.springbatch.dto.BookCommentDto;

@Configuration
public class ImportCommentsConfig extends AbstractMongoImport<BookCommentDto> {

    private static final String STEP_NAME = "importCommentsStep";
    private static final String FLOW_NAME = "importCommentsFlow";
    private static final String READER_NAME = "importCommentsReader";
    private static final String SOURCE_COLLECTION_NAME = "book_comments";
    private static final String SOURCE_COLLECTION_QUERY = "{}";
    private static final String INSERT_SQL = "INSERT INTO public.book_comments(id, book_id, text) VALUES (:id, :bookId, :text)";

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
    public Step importCommentsStep(JobRepository jobRepository, PlatformTransactionManager transactionManager, MongoItemReader<BookCommentDto> commentsReader, JdbcBatchItemWriter<BookCommentDto> commentsWriter) {
        return getStep(STEP_NAME, jobRepository, transactionManager, commentsReader, commentsWriter);
    }

    @StepScope
    @Bean
    public MongoItemReader<BookCommentDto> commentsReader(MongoTemplate mongoTemplate) {
        return getBuilder(mongoTemplate).targetType(BookCommentDto.class).build();
    }

    @StepScope
    @Bean
    public JdbcBatchItemWriter<BookCommentDto> commentsWriter(JdbcBatchItemWriterBuilder<BookCommentDto> jdbcBatchItemWriterBuilder) {
        return getWriter(INSERT_SQL, jdbcBatchItemWriterBuilder);
    }
}
