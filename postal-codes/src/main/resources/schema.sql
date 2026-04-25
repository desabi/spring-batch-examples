-- ============================================================
-- SCHEMA: POSTAL CODES BATCH SYSTEM
-- ============================================================
-- This script defines the database structure required for:
-- - Storing postal code data
-- - Supporting Spring Batch metadata tables
-- - Ensuring data integrity and performance
--
-- IMPORTANT:
-- This script is designed for H2 database (file-based mode).
-- ============================================================


-- ============================================================
-- 1. MAIN BUSINESS TABLE
-- ============================================================

-- Table: postal_codes
-- Purpose:
-- Stores postal code records ingested from CSV files.
-- This is the core table that the batch job reads/writes.

CREATE TABLE IF NOT EXISTS postal_codes (

    -- Primary key (auto-generated)
                                            id BIGINT AUTO_INCREMENT PRIMARY KEY,

    -- Unique postal code identifier
    -- Must be unique across all records
                                            postal_code VARCHAR(10) NOT NULL,

    -- City name associated with the postal code
    city_name VARCHAR(100) NOT NULL,

    -- State name associated with the postal code
    state_name VARCHAR(100) NOT NULL,

    -- Constraint: enforce uniqueness of postal codes
    CONSTRAINT uk_postal_code UNIQUE (postal_code)
    );

-- ============================================================
-- INDEXES (Performance Optimization)
-- ============================================================

-- Index for faster lookups by postal code
-- Critical for:
-- - UPDATE detection
-- - Existence checks in processor
CREATE INDEX IF NOT EXISTS idx_postal_code
    ON postal_codes (postal_code);

-- ============================================================
-- 2. SPRING BATCH METADATA TABLES
-- ============================================================
-- These tables are REQUIRED by Spring Batch to:
-- - Track job executions
-- - Store job parameters
-- - Handle restarts and failures
--
-- NOTE:
-- Spring Boot can auto-create these tables, but defining them
-- explicitly gives you control and visibility.
-- ============================================================


-- ============================================================
-- Table: BATCH_JOB_INSTANCE
-- ============================================================
CREATE TABLE IF NOT EXISTS BATCH_JOB_INSTANCE (
                                                  JOB_INSTANCE_ID BIGINT PRIMARY KEY,
                                                  VERSION BIGINT,
                                                  JOB_NAME VARCHAR(100) NOT NULL,
    JOB_KEY VARCHAR(32) NOT NULL,
    CONSTRAINT JOB_INST_UN UNIQUE (JOB_NAME, JOB_KEY)
    );


-- ============================================================
-- Table: BATCH_JOB_EXECUTION
-- ============================================================
CREATE TABLE IF NOT EXISTS BATCH_JOB_EXECUTION (
                                                   JOB_EXECUTION_ID BIGINT PRIMARY KEY,
                                                   VERSION BIGINT,
                                                   JOB_INSTANCE_ID BIGINT NOT NULL,
                                                   CREATE_TIME TIMESTAMP NOT NULL,
                                                   START_TIME TIMESTAMP,
                                                   END_TIME TIMESTAMP,
                                                   STATUS VARCHAR(10),
    EXIT_CODE VARCHAR(2500),
    EXIT_MESSAGE VARCHAR(2500),
    LAST_UPDATED TIMESTAMP,
    CONSTRAINT JOB_INST_EXEC_FK FOREIGN KEY (JOB_INSTANCE_ID)
    REFERENCES BATCH_JOB_INSTANCE(JOB_INSTANCE_ID)
    );


-- ============================================================
-- Table: BATCH_JOB_EXECUTION_PARAMS
-- ============================================================
CREATE TABLE IF NOT EXISTS BATCH_JOB_EXECUTION_PARAMS (
                                                          JOB_EXECUTION_ID BIGINT NOT NULL,
                                                          PARAMETER_NAME VARCHAR(100) NOT NULL,
    PARAMETER_TYPE VARCHAR(100) NOT NULL,
    PARAMETER_VALUE VARCHAR(2500),
    IDENTIFYING CHAR(1) NOT NULL,
    CONSTRAINT JOB_EXEC_PARAMS_FK FOREIGN KEY (JOB_EXECUTION_ID)
    REFERENCES BATCH_JOB_EXECUTION(JOB_EXECUTION_ID)
    );


-- ============================================================
-- Table: BATCH_STEP_EXECUTION
-- ============================================================
CREATE TABLE IF NOT EXISTS BATCH_STEP_EXECUTION (
                                                    STEP_EXECUTION_ID BIGINT PRIMARY KEY,
                                                    VERSION BIGINT NOT NULL,
                                                    STEP_NAME VARCHAR(100) NOT NULL,
    JOB_EXECUTION_ID BIGINT NOT NULL,
    CREATE_TIME TIMESTAMP NOT NULL,
    START_TIME TIMESTAMP,
    END_TIME TIMESTAMP,
    STATUS VARCHAR(10),
    COMMIT_COUNT BIGINT,
    READ_COUNT BIGINT,
    FILTER_COUNT BIGINT,
    WRITE_COUNT BIGINT,
    READ_SKIP_COUNT BIGINT,
    WRITE_SKIP_COUNT BIGINT,
    PROCESS_SKIP_COUNT BIGINT,
    ROLLBACK_COUNT BIGINT,
    EXIT_CODE VARCHAR(2500),
    EXIT_MESSAGE VARCHAR(2500),
    LAST_UPDATED TIMESTAMP,
    CONSTRAINT JOB_EXEC_STEP_FK FOREIGN KEY (JOB_EXECUTION_ID)
    REFERENCES BATCH_JOB_EXECUTION(JOB_EXECUTION_ID)
    );


-- ============================================================
-- Table: BATCH_STEP_EXECUTION_CONTEXT
-- ============================================================
CREATE TABLE IF NOT EXISTS BATCH_STEP_EXECUTION_CONTEXT (
                                                            STEP_EXECUTION_ID BIGINT PRIMARY KEY,
                                                            SHORT_CONTEXT VARCHAR(2500),
    SERIALIZED_CONTEXT CLOB,
    CONSTRAINT STEP_EXEC_CTX_FK FOREIGN KEY (STEP_EXECUTION_ID)
    REFERENCES BATCH_STEP_EXECUTION(STEP_EXECUTION_ID)
    );


-- ============================================================
-- Table: BATCH_JOB_EXECUTION_CONTEXT
-- ============================================================
CREATE TABLE IF NOT EXISTS BATCH_JOB_EXECUTION_CONTEXT (
                                                           JOB_EXECUTION_ID BIGINT PRIMARY KEY,
                                                           SHORT_CONTEXT VARCHAR(2500),
    SERIALIZED_CONTEXT CLOB,
    CONSTRAINT JOB_EXEC_CTX_FK FOREIGN KEY (JOB_EXECUTION_ID)
    REFERENCES BATCH_JOB_EXECUTION(JOB_EXECUTION_ID)
    );


-- ============================================================
-- 3. SEQUENCES (Required for Spring Batch IDs in H2)
-- ============================================================

CREATE SEQUENCE IF NOT EXISTS BATCH_JOB_SEQ START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE IF NOT EXISTS BATCH_JOB_EXECUTION_SEQ START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE IF NOT EXISTS BATCH_STEP_EXECUTION_SEQ START WITH 1 INCREMENT BY 1;



-- ============================================================
-- 4. OPTIONAL INITIAL DATA (FOR TESTING)
-- ============================================================

-- Example seed data (can be removed in production)
INSERT INTO postal_codes (postal_code, city_name, state_name)
VALUES ('00000', 'SampleCity', 'SampleState');