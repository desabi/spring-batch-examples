package com.desabi.guide.spring.batch.feecalculator.processor;

import com.desabi.guide.spring.batch.feecalculator.model.CustomerInput;
import com.desabi.guide.spring.batch.feecalculator.model.CustomerOutput;
import com.desabi.guide.spring.batch.feecalculator.util.FeeCalculator;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

/**
 * Spring Batch {@link ItemProcessor} that delegates to
 * {@link FeeCalculator#process(CustomerInput)} to validate and enrich
 * customer records.
 *
 * <p>If the email is invalid, the {@link com.desabi.guide.spring.batch.feecalculator.exception.InvalidEmailException}
 * will be thrown and is expected to be handled by the step's skip policy.</p>
 *
 * <p><b>Communicates with:</b></p>
 * <ul>
 *   <li>{@link FeeCalculator} – static delegate for business logic</li>
 *   <li>{@link CustomerInput} – input type</li>
 *   <li>{@link CustomerOutput} – output type</li>
 * </ul>
 */
@Component
public class CustomerItemProcessor implements ItemProcessor<CustomerInput, CustomerOutput> {

    /**
     * Processes a single customer record.
     *
     * @param item the input record read from the CSV
     * @return the enriched output record ready for persistence
     * @throws com.desabi.guide.spring.batch.feecalculator.exception.InvalidEmailException
     *         if the email address does not contain an {@code @}
     */
    @Override
    public CustomerOutput process(CustomerInput item) throws Exception {
        return FeeCalculator.process(item);
    }
}