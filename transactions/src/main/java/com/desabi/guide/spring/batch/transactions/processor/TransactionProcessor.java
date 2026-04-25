package com.desabi.guide.spring.batch.transactions.processor;

import com.desabi.guide.spring.batch.transactions.model.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.lang.NonNull;

/**
 * Custom processor that implements filtering and enrichment logic.
 * Implements FR-04 through FR-08.
 */
public class TransactionProcessor implements ItemProcessor<Transaction, Transaction> {

    private static final Logger log = LoggerFactory.getLogger(TransactionProcessor.class);

    /**
     * Processes a single transaction.
     * @param item The raw transaction read from the CSV.
     * @return The enriched transaction, or null if the transaction is filtered out.
     */
    @Override
    public Transaction process(@NonNull Transaction item) {
        // FR-04: Filter out amounts <= 0
        if (item.getAmount() <= 0) {
            return null; 
        }

        // FR-05, FR-06, FR-07: Enrich with status based on amount
        if (item.getAmount() > 10000) {
            item.setStatus("HIGH_VALUE");
        } else {
            item.setStatus("NORMAL");
        }

        // FR-08: Log progress
        log.info("Processed Transaction: {} | Status: {}", item.getTransactionId(), item.getStatus());

        return item;
    }
}