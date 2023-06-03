package ru.otus.spring.springbatch.job;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.*;
import org.springframework.batch.core.job.builder.FlowBuilder;
import org.springframework.batch.core.job.flow.Flow;
import org.springframework.batch.core.job.flow.support.SimpleFlow;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.data.MongoItemReader;
import org.springframework.batch.item.data.builder.MongoItemReaderBuilder;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.lang.NonNull;
import org.springframework.transaction.PlatformTransactionManager;

import java.util.HashMap;

import static ru.otus.spring.springbatch.job.ImportJobConfig.CHUNK_SIZE;

@Slf4j
@SuppressWarnings("")
public abstract class AbstractMongoImport<T> {

    private static final String SKIP_ON_READ = "Item reading skipped";
    private static final String SKIP_ON_PROCESS = "Item processing skipped";
    private static final String SKIP_ON_WRITE = "Item writing skipped";
    private static final String READ_ERROR_MESSAGE = "Item read error";
    private static final String PROCESS_ERROR_MESSAGE = "Process error";
    private static final String WRITE_ERROR_MESSAGE = "Write error";
    private static final String CHUNK_ERROR_MESSAGE = "Chunk error";

    protected abstract String getFlowName();

    protected abstract String getReaderName();

    protected abstract String getSourceName();

    protected abstract String getSourceQuery();

    protected Flow getFlow(Step step) {
        return new FlowBuilder<SimpleFlow>(getFlowName())
                .start(step)
                .build();
    }

    protected Step getStep(String name, JobRepository jobRepository, PlatformTransactionManager transactionManager, MongoItemReader<T> reader, JdbcBatchItemWriter<T> writer) {
        return new StepBuilder(name, jobRepository)
                .<T, T>chunk(CHUNK_SIZE, transactionManager)
                .reader(reader)
                .writer(writer)
                .listener(new SkipListener<>() {
                    @Override
                    public void onSkipInRead(Throwable t) {
                        log.info(SKIP_ON_READ);
                    }

                    @Override
                    public void onSkipInProcess(Object item, Throwable t) {
                        log.info(SKIP_ON_PROCESS);
                    }

                    @Override
                    public void onSkipInWrite(Object item, Throwable t) {
                        log.info(SKIP_ON_WRITE);
                    }
                })
                .listener(new ItemReadListener<>() {
                    @Override
                    public void onReadError(@NonNull Exception e) {
                        log.info(READ_ERROR_MESSAGE);
                    }
                })
                .listener(new ItemProcessListener<>() {
                    @Override
                    public void onProcessError(@NonNull T o, @NonNull Exception e) {
                        log.info(PROCESS_ERROR_MESSAGE);
                    }
                })
                .listener(new ItemWriteListener<>() {
                    @Override
                    public void onWriteError(Exception exception, Chunk<? extends T> items) {
                        log.info(WRITE_ERROR_MESSAGE);
                    }
                })
                .listener(new ChunkListener() {
                    @Override
                    public void afterChunkError(@NonNull ChunkContext chunkContext) {
                        log.info(CHUNK_ERROR_MESSAGE);
                    }
                })
                .build();
    }

    protected MongoItemReaderBuilder<T> getBuilder(MongoTemplate mongoTemplate) {
        return new MongoItemReaderBuilder<T>()
                .name(getReaderName())
                .template(mongoTemplate)
                .collection(getSourceName())
                .jsonQuery(getSourceQuery())
                .sorts(new HashMap<>());
    }

    protected JdbcBatchItemWriter<T> getWriter(String sql, JdbcBatchItemWriterBuilder<T> builder) {
        return builder.sql(sql).build();
    }
}
