package com.desabi.guide.spring.batch.postalcodes.processor;

import com.desabi.guide.spring.batch.postalcodes.domain.*;
import com.desabi.guide.spring.batch.postalcodes.repository.PostalCodeRepository;
import org.springframework.batch.item.ItemProcessor;

import java.util.HashSet;
import java.util.Set;

/**
 * Processes each RowRecord:
 * - Validates fields
 * - Detects duplicates
 * - Determines INSERT or UPDATE
 */
public class PostalCodeProcessor implements ItemProcessor<RowRecord, ProcessedRecord> {

    private final PostalCodeRepository repository;
    private final Set<String> seen = new HashSet<>();

    public PostalCodeProcessor(PostalCodeRepository repository) {
        this.repository = repository;
    }

    @Override
    public ProcessedRecord process(RowRecord item) {
        if (item.getPostalCode() == null || item.getPostalCode().isBlank() ||
            item.getCityName() == null || item.getCityName().isBlank() ||
            item.getStateName() == null || item.getStateName().isBlank()) {
            return new ProcessedRecord(ProcessedRecord.OperationType.SKIP, item);
        }

        if (!seen.add(item.getPostalCode())) {
            return new ProcessedRecord(ProcessedRecord.OperationType.SKIP, item);
        }

        var existing = repository.findByPostalCode(item.getPostalCode());

        if (existing.isEmpty()) {
            return new ProcessedRecord(ProcessedRecord.OperationType.INSERT, item);
        }

        var entity = existing.get();
        if (entity.getCityName().equals(item.getCityName()) &&
            entity.getStateName().equals(item.getStateName())) {
            return new ProcessedRecord(ProcessedRecord.OperationType.SKIP, item);
        }

        return new ProcessedRecord(ProcessedRecord.OperationType.UPDATE, item);
    }
}