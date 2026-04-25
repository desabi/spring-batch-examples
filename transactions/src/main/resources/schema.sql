DROP TABLE IF EXISTS PROCESSED_TRANSACTIONS;

CREATE TABLE PROCESSED_TRANSACTIONS (
                                        TRANSACTION_ID VARCHAR(50) PRIMARY KEY,
                                        ACCOUNT_NUMBER VARCHAR(50) NOT NULL,
                                        AMOUNT DECIMAL(10, 2) NOT NULL,
                                        TRANSACTION_DATE TIMESTAMP NOT NULL,
                                        STATUS VARCHAR(20) NOT NULL
);