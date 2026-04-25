package com.desabi.guide.spring.batch.feecalculator.exception;

/**
 * Thrown by {@link com.desabi.guide.spring.batch.feecalculator.util.FeeCalculator#process}
 * when the email field of a customer record does not contain an {@code @} character.
 *
 * <p>This exception is used in the skip logic of the
 * {@link com.desabi.guide.spring.batch.feecalculator.config.BatchConfig}
 * step definition, allowing the batch job to skip invalid records without failing.</p>
 *
 * <p><b>Communicates with:</b></p>
 * <ul>
 *   <li>{@link com.desabi.guide.spring.batch.feecalculator.util.FeeCalculator} – the thrower</li>
 *   <li>Spring Batch step configuration – caught by the skip policy</li>
 * </ul>
 */
public class InvalidEmailException extends RuntimeException {
    /**
     * Constructs a new {@code InvalidEmailException} with a descriptive message.
     *
     * @param message detail message explaining why the email is invalid
     */
    public InvalidEmailException(String message) {
        super(message);
    }
}