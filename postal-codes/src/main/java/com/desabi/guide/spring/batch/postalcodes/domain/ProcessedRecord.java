package com.desabi.guide.spring.batch.postalcodes.domain;

import lombok.Getter;

/**
 * Represents the result after processing a record.
 */
@Getter
public class ProcessedRecord {

    public enum OperationType { INSERT, UPDATE, SKIP }

    private final OperationType operationType;
    private final RowRecord record;

    public ProcessedRecord(OperationType operationType, RowRecord record) {
        this.operationType = operationType;
        this.record = record;
    }
}