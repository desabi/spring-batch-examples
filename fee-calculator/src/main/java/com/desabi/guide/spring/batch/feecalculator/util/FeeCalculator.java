package com.desabi.guide.spring.batch.feecalculator.util;

import com.desabi.guide.spring.batch.feecalculator.exception.InvalidEmailException;
import com.desabi.guide.spring.batch.feecalculator.model.CustomerInput;
import com.desabi.guide.spring.batch.feecalculator.model.CustomerOutput;

import java.math.BigDecimal;
import java.util.Map;

/**
 * Pure business logic that validates the email format and applies
 * country‑specific onboarding fees.
 *
 * <p>This class is deliberately kept outside the Spring context as a
 * stateless utility so it can be unit‑tested independently of the
 * batch infrastructure.</p>
 *
 * <p><b>Communicates with:</b></p>
 * <ul>
 *   <li>{@link CustomerInput} – input parameter</li>
 *   <li>{@link CustomerOutput} – produced output</li>
 *   <li>{@link InvalidEmailException} – thrown on invalid email</li>
 * </ul>
 */
public final class FeeCalculator {

    /** Fee mapping table: country code → fee amount + currency */
    private static final Map<String, FeeEntry> FEE_MAP = Map.of(
            "USA", new FeeEntry(new BigDecimal("25.00"), "USD"),
            "DEU", new FeeEntry(new BigDecimal("19.00"), "EUR"),
            "MEX", new FeeEntry(new BigDecimal("200.00"), "MXN")
    );

    /** Default fee applied when country is unknown or missing */
    private static final FeeEntry DEFAULT_FEE = new FeeEntry(new BigDecimal("50.00"), "USD");

    private FeeCalculator() {}

    /**
     * Validates the customer email and calculates the appropriate onboarding fee.
     *
     * @param input a raw customer record from the CSV
     * @return an enriched {@link CustomerOutput} with full name, fee and currency
     * @throws InvalidEmailException if the email does not contain {@code @}
     */
    public static CustomerOutput process(CustomerInput input) throws InvalidEmailException {
        // US-02: Email validation
        if (input.getEmail() == null || !input.getEmail().contains("@")) {
            throw new InvalidEmailException("Invalid email format: " + input.getEmail());
        }

        // US-03: Country fee mapping
        String country = input.getCountry();
        FeeEntry fee = (country != null) ? FEE_MAP.getOrDefault(country, DEFAULT_FEE) : DEFAULT_FEE;

        String fullName = input.getFirstName() + " " + input.getLastName();

        return new CustomerOutput(
                input.getEmail(),
                fullName,
                country,
                fee.amount,
                fee.currency
        );
    }

    /**
     * Immutable data holder for a fee amount and its currency.
     */
    private record FeeEntry(BigDecimal amount, String currency) {}
}