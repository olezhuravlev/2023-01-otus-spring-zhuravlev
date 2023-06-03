package ru.otus.spring.springbatch.shell;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.core.launch.JobOperator;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;

import java.util.Date;
import java.util.Properties;

import static ru.otus.spring.springbatch.job.ImportJobConfig.IMPORT_JOB_NAME;

@RequiredArgsConstructor
@ShellComponent
public class BatchCommands {

    private final JobOperator jobOperator;
    private final JobExplorer jobExplorer;

    @ShellMethod(value = "startMigrationJobWithJobOperator", key = "import")
    public void startMigrationJobWithJobOperator() throws Exception {
        Properties properties = new Properties();
        properties.put("date", String.valueOf(new Date()));
        Long executionId = jobOperator.start(IMPORT_JOB_NAME, properties);
        System.out.println(jobOperator.getSummary(executionId));
    }

    @ShellMethod(value = "showInfo", key = "info")
    public void showInfo() {
        System.out.println(jobExplorer.getJobNames());
        System.out.println(jobExplorer.getLastJobInstance(IMPORT_JOB_NAME));
    }
}
