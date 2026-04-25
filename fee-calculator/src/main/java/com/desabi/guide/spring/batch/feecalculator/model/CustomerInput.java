package com.desabi.guide.spring.batch.feecalculator.model;

/**
 * Represents a single row from the input CSV file.
 *
 * <p>Used by the {@code FlatFileItemReader} which maps CSV fields
 * onto the fields of this class via {@link BeanWrapperFieldSetMapper}.</p>
 *
 * <p><b>Communicates with:</b></p>
 * <ul>
 *   <li>{@link com.desabi.guide.spring.batch.feecalculator.config.BatchConfig}
 *       – instantiated by the reader</li>
 *   <li>{@link com.desabi.guide.spring.batch.feecalculator.util.FeeCalculator}
 *       – consumed as input for processing</li>
 *   <li>{@link com.desabi.guide.spring.batch.feecalculator.processor.CustomerItemProcessor}
 *       – passed by the step infrastructure</li>
 * </ul>
 */
public class CustomerInput {
    private String firstName;
    private String lastName;
    private String email;
    private String country;

    // Getters and setters (required for BeanWrapperFieldSetMapper)
    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getCountry() { return country; }
    public void setCountry(String country) { this.country = country; }

    @Override
    public String toString() {
        return String.format("CustomerInput[firstName=%s, lastName=%s, email=%s, country=%s]",
                firstName, lastName, email, country);
    }
}