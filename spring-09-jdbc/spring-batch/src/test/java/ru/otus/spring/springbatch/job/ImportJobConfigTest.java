package ru.otus.spring.springbatch.job;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.batch.test.JobRepositoryTestUtils;
import org.springframework.batch.test.context.SpringBatchTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.JdbcDatabaseContainer;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;
import org.testcontainers.utility.MountableFile;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.Map.entry;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
@SpringBatchTest
class ImportJobConfigTest {

    @Autowired
    private JobLauncherTestUtils jobLauncherTestUtils;

    @Autowired
    private JobRepositoryTestUtils jobRepositoryTestUtils;

    private static final String MONGO_IMAGE_NAME = "mongo:6.0.5";
    private static final String POSTGRES_IMAGE_NAME = "postgres:15.3";
    private static final String DATABASE_NAME = "librarydb";
    private static final String USERNAME = "librarydb";
    private static final String PASSWORD = "librarydb";

    private static final List<Map<String, String>> EXPECTED_AUTHORS = new ArrayList<>();
    private static final List<Map<String, String>> EXPECTED_GENRES = new ArrayList<>();
    private static final List<Map<String, String>> EXPECTED_BOOKS = new ArrayList<>();
    private static final List<Map<String, String>> EXPECTED_COMMENTS = new ArrayList<>();

    static MongoDBContainer mongo = new MongoDBContainer(DockerImageName.parse(MONGO_IMAGE_NAME))
            .withCopyFileToContainer(MountableFile.forClasspathResource("/mongo-init"), "/initdb")
            .withStartupTimeout(Duration.ofSeconds(3))
            .waitingFor(Wait.forListeningPort());

    static JdbcDatabaseContainer<?> postgres = new PostgreSQLContainer<>(DockerImageName.parse(POSTGRES_IMAGE_NAME))
            .withDatabaseName(DATABASE_NAME)
            .withUsername(USERNAME)
            .withPassword(PASSWORD)
            .withExposedPorts(5432)
            .withInitScript("pg-init/pg_schema_and_data.sql")
            .waitingFor(Wait.forListeningPort());

    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry registry) throws Exception {

        mongo.start();
        mongo.execInContainer("./initdb/mongoimport.sh");

        postgres.start();

        registry.add("spring.data.mongodb.host", mongo::getHost);
        registry.add("spring.data.mongodb.port", mongo::getFirstMappedPort);
        registry.add("spring.data.mongodb.connectionString", mongo::getConnectionString);

        registry.add("spring.datasource.host", postgres::getHost);
        registry.add("spring.datasource.port", postgres::getFirstMappedPort);
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @BeforeAll
    static void beforeAll() {

        EXPECTED_AUTHORS.add(Map.ofEntries(entry("id", "1"), entry("name", "Test Author 1")));
        EXPECTED_AUTHORS.add(Map.ofEntries(entry("id", "2"), entry("name", "Test Author 2")));

        EXPECTED_GENRES.add(Map.ofEntries(entry("id", "1"), entry("name", "Test Genre 1")));
        EXPECTED_GENRES.add(Map.ofEntries(entry("id", "2"), entry("name", "Test Genre 2")));

        EXPECTED_BOOKS.add(Map.ofEntries(entry("id", "1"), entry("title", "Test Book 1"), entry("author_id", "1"), entry("genre_id", "1")));
        EXPECTED_BOOKS.add(Map.ofEntries(entry("id", "2"), entry("title", "Test Book 2"), entry("author_id", "1"), entry("genre_id", "1")));
        EXPECTED_BOOKS.add(Map.ofEntries(entry("id", "3"), entry("title", "Test Book 3"), entry("author_id", "1"), entry("genre_id", "2")));

        EXPECTED_COMMENTS.add(Map.ofEntries(entry("id", "1"), entry("book_id", "1"), entry("text", "Test Comment 1 (Book 1)")));
        EXPECTED_COMMENTS.add(Map.ofEntries(entry("id", "2"), entry("book_id", "1"), entry("text", "Test Comment 2 (Book 1)")));
        EXPECTED_COMMENTS.add(Map.ofEntries(entry("id", "3"), entry("book_id", "2"), entry("text", "Test Comment 3 (Book 2)")));
        EXPECTED_COMMENTS.add(Map.ofEntries(entry("id", "4"), entry("book_id", "2"), entry("text", "Test Comment 4 (Book 2)")));
    }

    @BeforeEach
    void clearMetaData() {
        jobRepositoryTestUtils.removeJobExecutions();
    }

    @Test
    void testJob(@Autowired Job importJob) throws Exception {

        this.jobLauncherTestUtils.setJob(importJob);

        JobExecution jobExecution = jobLauncherTestUtils.launchJob();
        assertThat(jobExecution.getExitStatus().getExitCode()).isEqualTo("COMPLETED");

        ResultSet authorsRS = performQuery(postgres, "SELECT * from public.authors;");
        assertThat(toList(authorsRS)).containsExactlyInAnyOrderElementsOf(EXPECTED_AUTHORS);

        ResultSet genresRS = performQuery(postgres, "SELECT * from public.genres;");
        assertThat(toList(genresRS)).containsExactlyInAnyOrderElementsOf(EXPECTED_GENRES);

        ResultSet booksRS = performQuery(postgres, "SELECT * from public.books;");
        assertThat(toList(booksRS)).containsExactlyInAnyOrderElementsOf(EXPECTED_BOOKS);

        ResultSet commentsRS = performQuery(postgres, "SELECT * from public.book_comments;");
        assertThat(toList(commentsRS)).containsExactlyInAnyOrderElementsOf(EXPECTED_COMMENTS);
    }

    private ResultSet performQuery(JdbcDatabaseContainer<?> container, String sql) throws SQLException {
        DataSource ds = getDataSource(container);
        Statement statement = ds.getConnection().createStatement();
        statement.execute(sql);
        return statement.getResultSet();
    }

    private DataSource getDataSource(JdbcDatabaseContainer<?> container) {
        HikariConfig hikariConfig = new HikariConfig();
        hikariConfig.setJdbcUrl(container.getJdbcUrl());
        hikariConfig.setUsername(container.getUsername());
        hikariConfig.setPassword(container.getPassword());
        hikariConfig.setDriverClassName(container.getDriverClassName());
        return new HikariDataSource(hikariConfig);
    }

    private List<Map<String, String>> toList(ResultSet resultSet) throws SQLException {

        List<Map<String, String>> result = new ArrayList<>();

        int columnsCount = resultSet.getMetaData().getColumnCount();
        if (columnsCount == 0) {
            return result;
        }

        while (resultSet.next()) {
            Map<String, String> row = new HashMap<>();
            for (int columnId = 1; columnId <= columnsCount; columnId++) {
                String columnName = resultSet.getMetaData().getColumnName(columnId);
                String columnValue = resultSet.getString(columnId);
                row.put(columnName, columnValue);
            }
            result.add(row);
        }

        return result;
    }
}
