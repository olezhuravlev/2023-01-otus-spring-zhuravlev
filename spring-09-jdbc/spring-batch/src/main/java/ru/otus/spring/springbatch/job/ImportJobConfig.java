package ru.otus.spring.springbatch.job;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.FlowBuilder;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.job.flow.Flow;
import org.springframework.batch.core.job.flow.support.SimpleFlow;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;

import javax.sql.DataSource;

@Configuration
@SuppressWarnings({"rawtypes", "unchecked"})
public class ImportJobConfig {

    public static final int CHUNK_SIZE = 5;
    public static final String IMPORT_JOB_NAME = "importJob";

    @Bean
    public Job importJob(JobRepository jobRepository, Flow splitFlow, Step importBooksStep, Step importBooksAclObjectStep, Step importBooksAclEntryStep, Step importCommentsStep) {
        return new JobBuilder(IMPORT_JOB_NAME, jobRepository)
                .start(splitFlow)
                .next(importBooksStep)
                .next(importBooksAclObjectStep)
                .next(importBooksAclEntryStep)
                .next(importCommentsStep)
                .build()
                .build();
    }

    @Bean
    public Flow splitFlow(@Qualifier("taskExecutor") TaskExecutor taskExecutor, Flow authorsFlow, Flow genresFlow) {
        return new FlowBuilder<SimpleFlow>("splitFlow")
                .split(taskExecutor)
                .add(authorsFlow, genresFlow)
                .build();
    }

    @StepScope
    @Bean
    public JdbcBatchItemWriterBuilder jdbcBatchItemWriterBuilder(DataSource dataSource) {
        return new JdbcBatchItemWriterBuilder()
                .dataSource(dataSource)
                .itemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<>());
    }
}
