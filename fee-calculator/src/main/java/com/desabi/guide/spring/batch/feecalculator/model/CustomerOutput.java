package com.desabi.guide.spring.batch.feecalculator.model;

import java.math.BigDecimal;

/**
 * Enriched output record that is written to the {@code onboarding_fee_audit} table.
 *
 * <p>Contains the original email, the concatenated full name, the country code,
 * the calculated fee amount, and its currency.</p>
 *
 * <p><b>Communicates with:</b></p>
 * <ul>
 *   <li>{@link com.desabi.guide.spring.batch.feecalculator.util.FeeCalculator}
 *       – creates instances of this class</li>
 *   <li>{@link com.desabi.guide.spring.batch.feecalculator.config.BatchConfig}
 *       – the {@code JdbcBatchItemWriter} uses this type</li>
 * </ul>
 */
public class CustomerOutput {
    private String email;
    private String fullName;
    private String countryCode;
    private BigDecimal calculatedFee;
    private String feeCurrency;

    public CustomerOutput() {}

    /**
     * Full constructor for creating a new {@code CustomerOutput}.
     *
     * @param email         the customer email
     * @param fullName      first name + space + last name
     * @param countryCode   original country code (ISO 3166-1 alpha-3)
     * @param calculatedFee the fee computed by the processor
     * @param feeCurrency   currency code (e.g. USD, EUR, MXN)
     */
    public CustomerOutput(String email, String fullName, String countryCode, 
                          BigDecimal calculatedFee, String feeCurrency) {
        this.email = email;
        this.fullName = fullName;
        this.countryCode = countryCode;
        this.calculatedFee = calculatedFee;
        this.feeCurrency = feeCurrency;
    }

    // Getters and setters
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getCountryCode() { return countryCode; }
    public void setCountryCode(String countryCode) { this.countryCode = countryCode; }

    public BigDecimal getCalculatedFee() { return calculatedFee; }
    public void setCalculatedFee(BigDecimal calculatedFee) { this.calculatedFee = calculatedFee; }

    public String getFeeCurrency() { return feeCurrency; }
    public void setFeeCurrency(String feeCurrency) { this.feeCurrency = feeCurrency; }
}