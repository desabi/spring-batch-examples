-- Business table for onboarding fee audit.
-- This table stores the enriched customer records after fee calculation.
-- The UNIQUE constraint on 'email' enables the MERGE (upsert) operation
-- used by the JdbcBatchItemWriter, guaranteeing idempotency.
CREATE TABLE IF NOT EXISTS onboarding_fee_audit (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    email VARCHAR(255) UNIQUE NOT NULL,
    full_name VARCHAR(255),
    country_code CHAR(3),
    calculated_fee DECIMAL(10,2),
    fee_currency VARCHAR(3),
    processed_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);