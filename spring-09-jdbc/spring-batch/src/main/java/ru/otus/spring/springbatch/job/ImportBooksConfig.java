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
import ru.otus.spring.springbatch.dto.BookDto;

@Configuration
@SuppressWarnings({"rawtypes", "unchecked"})
public class ImportBooksConfig extends AbstractMongoImport<BookDto> {

    private static final String STEP_NAME = "importBooksStep";
    private static final String FLOW_NAME = "importBooksFlow";
    private static final String READER_NAME = "importBooksReader";
    private static final String SOURCE_COLLECTION_NAME = "books";
    private static final String SOURCE_COLLECTION_QUERY = "{}";
    private static final String INSERT_SQL = "INSERT INTO public.books(id, title, author_id, genre_id) VALUES (:id, :title, :authorId, :genreId)";

    private static final String ACL_OBJECT_STEP_NAME = "importBooksAclObjectStep";
    private static final String INSERT_ACL_OBJECT_SQL = "INSERT INTO public.acl_object_identity(id, object_id_class, object_id_identity, parent_object, owner_sid, entries_inheriting) VALUES (:id, 1, :id, null, 1, true)";

    private static final String ACL_ENTRY_STEP_NAME = "importBooksAclEntryStep";
    private static final String INSERT_ACL_ENTRY_SQL = "INSERT INTO public.acl_entry(id, acl_object_identity, ace_order, sid, mask, granting, audit_success, audit_failure) VALUES (:id, :id, 0, 1, 1, true, true, true)";

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
    public Step importBooksStep(JobRepository jobRepository, PlatformTransactionManager transactionManager, MongoItemReader<BookDto> booksReader, JdbcBatchItemWriter<BookDto> booksWriter) {
        return getStep(STEP_NAME, jobRepository, transactionManager, booksReader, booksWriter);
    }

    @StepScope
    @Bean
    public MongoItemReader<BookDto> booksReader(MongoTemplate mongoTemplate) {
        return getBuilder(mongoTemplate).targetType(BookDto.class).build();
    }

    @StepScope
    @Bean
    public JdbcBatchItemWriter<BookDto> booksWriter(JdbcBatchItemWriterBuilder<BookDto> jdbcBatchItemWriterBuilder) {
        return getWriter(INSERT_SQL, jdbcBatchItemWriterBuilder);
    }


    /////////////////////////////////
    // ACLs

    @Bean
    public Step importBooksAclObjectStep(JobRepository jobRepository, PlatformTransactionManager transactionManager, MongoItemReader<BookDto> booksReader, JdbcBatchItemWriter booksAclObjectWriter) {
        return getStep(ACL_OBJECT_STEP_NAME, jobRepository, transactionManager, booksReader, booksAclObjectWriter);
    }

    @Bean
    public Step importBooksAclEntryStep(JobRepository jobRepository, PlatformTransactionManager transactionManager, MongoItemReader<BookDto> booksReader, JdbcBatchItemWriter booksAclEntryWriter) {
        return getStep(ACL_ENTRY_STEP_NAME, jobRepository, transactionManager, booksReader, booksAclEntryWriter);
    }

    @StepScope
    @Bean
    public JdbcBatchItemWriter booksAclObjectWriter(JdbcBatchItemWriterBuilder jdbcBatchItemWriterBuilder) {
        return getWriter(INSERT_ACL_OBJECT_SQL, jdbcBatchItemWriterBuilder);
    }

    @StepScope
    @Bean
    public JdbcBatchItemWriter booksAclEntryWriter(JdbcBatchItemWriterBuilder jdbcBatchItemWriterBuilder) {
        return getWriter(INSERT_ACL_ENTRY_SQL, jdbcBatchItemWriterBuilder);
    }
}
