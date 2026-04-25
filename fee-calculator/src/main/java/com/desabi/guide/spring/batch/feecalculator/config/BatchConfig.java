package com.desabi.guide.spring.batch.feecalculator.config;

import com.desabi.guide.spring.batch.feecalculator.exception.InvalidEmailException;
import com.desabi.guide.spring.batch.feecalculator.listener.JobCompletionNotificationListener;
import com.desabi.guide.spring.batch.feecalculator.model.CustomerInput;
import com.desabi.guide.spring.batch.feecalculator.model.CustomerOutput;
import com.desabi.guide.spring.batch.feecalculator.processor.CustomerItemProcessor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;

/**
 * Spring Batch configuration that wires together the {@code reader → processor → writer}
 * pipeline and defines the job and step beans.
 *
 * <p><b>Communicates with:</b></p>
 * <ul>
 *   <li>{@link FlatFileItemReader} – reads CSV data and produces
 *       {@link CustomerInput} objects</li>
 *   <li>{@link CustomerItemProcessor} – delegates to
 *       {@link com.desabi.guide.spring.batch.feecalculator.util.FeeCalculator}
 *       for validation and fee calculation</li>
 *   <li>{@link JdbcBatchItemWriter} – persists {@link CustomerOutput}
 *       entities to the database using a <em>MERGE</em> (upsert) statement</li>
 *   <li>{@link JobBuilder} / {@link StepBuilder} – construct the job
 *       and its single chunk‑oriented step</li>
 *   <li>{@link JobCompletionNotificationListener} – logs job metrics
 *       after execution</li>
 * </ul>
 */
@Configuration
public class BatchConfig {

  /**
   * Creates a step‑scoped {@link FlatFileItemReader} that reads the CSV
   * file specified by the {@code filePath} job parameter.
   *
   * <p>If the job parameter is absent (for example during test context
   * loading), a harmless dummy file is loaded from the classpath.</p>
   *
   * @param filePath        the fully qualified path to the input CSV (injected via SpEL)
   * @param resourceLoader  Spring resource loader (unused in current logic,
   *                        but kept for potential future classpath resources)
   * @return a configured {@code FlatFileItemReader<CustomerInput>}
   */
  @Bean
  @StepScope
  public FlatFileItemReader<CustomerInput> reader(
      @Value("#{jobParameters['filePath']}") String filePath,
      org.springframework.core.io.ResourceLoader resourceLoader) {

    Resource resource;
    if (filePath != null && !filePath.isEmpty()) {
      resource = new FileSystemResource(filePath);
    } else {
      resource = new ClassPathResource("dummy-for-test.csv");
    }

    return new FlatFileItemReaderBuilder<CustomerInput>()
        .name("customerCsvReader")
        .resource(resource)
        .delimited()
        .names("firstName", "lastName", "email", "country")
        .fieldSetMapper(new BeanWrapperFieldSetMapper<>() {{
          setTargetType(CustomerInput.class);
        }})
        .linesToSkip(1)
        .strict(false)
        .build();
  }

  /**
   * Provides the {@link ItemProcessor} that transforms {@code CustomerInput}
   * into {@code CustomerOutput}.
   *
   * @return an instance of {@link CustomerItemProcessor}
   * @see CustomerItemProcessor
   */
  @Bean
  public ItemProcessor<CustomerInput, CustomerOutput> processor() {
    return new CustomerItemProcessor();
  }

  /**
   * Configures a {@link JdbcBatchItemWriter} that performs an upsert operation
   * on the {@code onboarding_fee_audit} table using a SQL {@code MERGE} statement.
   *
   * <p>Keyed on the {@code email} column, it ensures idempotency and
   * restartability without duplicate key errors.</p>
   *
   * @param dataSource the configured {@link DataSource}
   * @return a {@code JdbcBatchItemWriter<CustomerOutput>}
   */
  @Bean
  public JdbcBatchItemWriter<CustomerOutput> writer(DataSource dataSource) {
    return new JdbcBatchItemWriterBuilder<CustomerOutput>()
        .dataSource(dataSource)
        .sql("""
                        MERGE INTO onboarding_fee_audit (email, full_name, country_code, calculated_fee, fee_currency)
                        KEY(email)
                        VALUES (?, ?, ?, ?, ?)
                        """)
        .itemPreparedStatementSetter((item, ps) -> {
          ps.setString(1, item.getEmail());
          ps.setString(2, item.getFullName());
          ps.setString(3, item.getCountryCode());
          ps.setBigDecimal(4, item.getCalculatedFee());
          ps.setString(5, item.getFeeCurrency());
        })
        .build();
  }

  /**
   * Builds the single step of the batch job.
   *
   * <p>The step uses chunk‑oriented processing with a chunk size of 10.
   * It is fault‑tolerant and will skip records that throw
   * {@link InvalidEmailException}.</p>
   *
   * @param jobRepository     the Spring Batch job repository
   * @param transactionManager the platform transaction manager
   * @param reader            the configured {@link ItemReader}
   * @param processor         the configured {@link ItemProcessor}
   * @param writer            the configured {@link ItemWriter}
   * @return a fully configured {@link Step}
   */
  @Bean
  public Step processCustomerStep(JobRepository jobRepository,
      PlatformTransactionManager transactionManager,
      ItemReader<CustomerInput> reader,
      ItemProcessor<CustomerInput, CustomerOutput> processor,
      ItemWriter<CustomerOutput> writer) {
    return new StepBuilder("processCustomerStep", jobRepository)
        .<CustomerInput, CustomerOutput>chunk(10, transactionManager)
        .reader(reader)
        .processor(processor)
        .writer(writer)
        .faultTolerant()
        .skip(InvalidEmailException.class)
        .skipLimit(1000)
        .build();
  }

  /**
   * Defines the main {@link Job} that runs the {@code processCustomerStep}.
   *
   * @param jobRepository        the job repository
   * @param processCustomerStep  the single step to execute
   * @param listener             the job execution listener for logging
   * @return a {@code Job} named {@code onboardingFeeJob}
   */
  @Bean
  public Job onboardingFeeJob(JobRepository jobRepository,
      Step processCustomerStep,
      JobCompletionNotificationListener listener) {
    return new JobBuilder("onboardingFeeJob", jobRepository)
        .start(processCustomerStep)
        .listener(listener)
        .build();
  }
}