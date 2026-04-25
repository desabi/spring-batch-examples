package com.desabi.guide.spring.batch.transactions.config;

import com.desabi.guide.spring.batch.transactions.model.Transaction;
import com.desabi.guide.spring.batch.transactions.processor.TransactionProcessor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;

import javax.sql.DataSource;
import java.time.format.DateTimeFormatter;

/**
 * Main configuration class for the Spring Batch Job.
 * Configures the Reader (US-01), Processor (US-02), and Writer (US-03).
 */
@Configuration
public class BatchConfig {

    @Value("${file.input.path}")
    private String inputPath;

    /**
     * Configures the FlatFileItemReader to read transactions from CSV.
     * Uses a custom LineMapper to handle date parsing.
     */
    @Bean
    public FlatFileItemReader<Transaction> reader() {
        return new FlatFileItemReaderBuilder<Transaction>()
                .name("transactionReader")
                .resource(new FileSystemResource(inputPath))
                .delimited()
                .names("transactionId", "accountNumber", "amount", "transactionDate")
                .fieldSetMapper(fieldSet -> {
                    Transaction transaction = new Transaction();
                    transaction.setTransactionId(fieldSet.readString("transactionId"));
                    transaction.setAccountNumber(fieldSet.readString("accountNumber"));
                    transaction.setAmount(fieldSet.readDouble("amount"));
                    
                    // FR-03: Parse date with yyyy-MM-dd HH:mm:ss
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                    transaction.setTransactionDate(java.time.LocalDateTime.parse(fieldSet.readString("transactionDate"), formatter));
                    return transaction;
                })
                .build();
    }

    @Bean
    public TransactionProcessor processor() {
        return new TransactionProcessor();
    }

    /**
     * Configures the JdbcBatchItemWriter to persist data to H2.
     */
    @Bean
    public JdbcBatchItemWriter<Transaction> writer(DataSource dataSource) {
        return new JdbcBatchItemWriterBuilder<Transaction>()
                .itemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<>())
                .sql("INSERT INTO PROCESSED_TRANSACTIONS (TRANSACTION_ID, ACCOUNT_NUMBER, AMOUNT, TRANSACTION_DATE, STATUS) " +
                     "VALUES (:transactionId, :accountNumber, :amount, :transactionDate, :status)")
                .dataSource(dataSource)
                .build();
    }

    /**
     * Defines the Step with a chunk size of 5 (TR-05).
     */
    @Bean
    public Step step1(JobRepository jobRepository, DataSourceTransactionManager transactionManager,
                      FlatFileItemReader<Transaction> reader, TransactionProcessor processor,
                      JdbcBatchItemWriter<Transaction> writer) {
        return new StepBuilder("importStep", jobRepository)
                .<Transaction, Transaction>chunk(5, transactionManager)
                .reader(reader)
                .processor(processor)
                .writer(writer)
                .build();
    }

    /**
     * Defines the Job (TR-03).
     */
    @Bean
    public Job transactionImportJob(JobRepository jobRepository, Step step1) {
        return new JobBuilder("transactionImportJob", jobRepository)
                .start(step1)
                .build();
    }

    @Bean
    public DataSourceTransactionManager transactionManager(DataSource dataSource) {
        return new DataSourceTransactionManager(dataSource);
    }
}