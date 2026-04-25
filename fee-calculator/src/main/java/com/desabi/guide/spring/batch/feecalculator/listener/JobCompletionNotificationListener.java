package com.desabi.guide.spring.batch.feecalculator.listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.stereotype.Component;

/**
 * Listens for the completion of the batch job and logs a summary of
 * the read, write, and skip counts.
 *
 * <p>Implements {@link JobExecutionListener} and is automatically registered
 * on the job bean defined in
 * {@link com.desabi.guide.spring.batch.feecalculator.config.BatchConfig}.</p>
 *
 * <p><b>Communicates with:</b></p>
 * <ul>
 *   <li>{@link JobExecution} – provides the step execution statistics</li>
 *   <li>{@link com.desabi.guide.spring.batch.feecalculator.config.BatchConfig}
 *       – the job builder attaches this listener to the job</li>
 * </ul>
 */
@Component
public class JobCompletionNotificationListener implements JobExecutionListener {

    private static final Logger log = LoggerFactory.getLogger(JobCompletionNotificationListener.class);

    /**
     * Called after the job execution finishes, regardless of its status.
     *
     * <p>When the job completes successfully it logs the read, write and skip
     * counts from the first (and only) step. If the job failed, it logs an error.</p>
     *
     * @param jobExecution the current {@link JobExecution}
     */
    @Override
    public void afterJob(JobExecution jobExecution) {
        if (jobExecution.getStatus() == BatchStatus.COMPLETED) {
            log.info("==============================================");
            log.info("JOB FINISHED SUCCESSFULLY!");
            log.info("Read Count:   {}", jobExecution.getStepExecutions().iterator().next().getReadCount());
            log.info("Write Count:  {}", jobExecution.getStepExecutions().iterator().next().getWriteCount());
            log.info("Skip Count:   {}", jobExecution.getStepExecutions().iterator().next().getSkipCount());
            log.info("==============================================");
        } else if (jobExecution.getStatus() == BatchStatus.FAILED) {
            log.error("JOB FAILED with status: {}", jobExecution.getStatus());
        }
    }
}