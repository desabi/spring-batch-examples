package com.desabi.guide.spring.batch.transactions.model;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Domain model representing a financial transaction.
 * Acts as the Data Transfer Object (DTO) between the Reader, Processor, and Writer.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Transaction {
    private String transactionId;
    private String accountNumber;
    private Double amount;
    private LocalDateTime transactionDate;
    private String status; // Enriched field: HIGH_VALUE or NORMAL
}