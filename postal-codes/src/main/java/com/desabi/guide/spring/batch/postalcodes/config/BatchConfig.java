package com.desabi.guide.spring.batch.postalcodes.config;

import com.desabi.guide.spring.batch.postalcodes.domain.ProcessedRecord;
import com.desabi.guide.spring.batch.postalcodes.domain.RowRecord;
import com.desabi.guide.spring.batch.postalcodes.processor.PostalCodeProcessor;
import com.desabi.guide.spring.batch.postalcodes.repository.PostalCodeRepository;
import com.desabi.guide.spring.batch.postalcodes.writer.PostalCodeWriter;
import org.springframework.batch.core.*;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.transaction.PlatformTransactionManager;

/**
 * Main Spring Batch configuration.
 */
@Configuration
@EnableBatchProcessing
public class BatchConfig {

    @Bean
    public FlatFileItemReader<RowRecord> reader() {
        FlatFileItemReader<RowRecord> reader = new FlatFileItemReader<>();
        reader.setResource(new FileSystemResource("${app.input.file}"));
        reader.setLinesToSkip(1);

        DefaultLineMapper<RowRecord> mapper = new DefaultLineMapper<>();
        DelimitedLineTokenizer tokenizer = new DelimitedLineTokenizer();
        tokenizer.setNames("postalCode", "cityName", "stateName");

        BeanWrapperFieldSetMapper<RowRecord> fieldSetMapper = new BeanWrapperFieldSetMapper<>();
        fieldSetMapper.setTargetType(RowRecord.class);

        mapper.setLineTokenizer(tokenizer);
        mapper.setFieldSetMapper(fieldSetMapper);

        reader.setLineMapper(mapper);
        return reader;
    }

    @Bean
    public PostalCodeProcessor processor(PostalCodeRepository repo) {
        return new PostalCodeProcessor(repo);
    }

    @Bean
    public PostalCodeWriter writer(PostalCodeRepository repo) {
        return new PostalCodeWriter(repo);
    }

    @Bean
    public Step step(JobRepository jobRepository, PlatformTransactionManager tx,
                     FlatFileItemReader<RowRecord> reader,
                     PostalCodeProcessor processor,
                     PostalCodeWriter writer) {

        return new StepBuilder("step", jobRepository)
                .<RowRecord, ProcessedRecord>chunk(10, tx)
                .reader(reader)
                .processor(processor)
                .writer(writer)
                .build();
    }

    @Bean
    public Job job(JobRepository jobRepository, Step step) {
        return new JobBuilder("job", jobRepository)
                .start(step)
                .build();
    }
}