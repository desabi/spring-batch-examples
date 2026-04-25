package com.desabi.guide.spring.batch.feecalculator;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

/**
 *
 * # 1. Build
 * mvnw.cmd clean package
 * # 2. Run the job
 * java -jar target\fee-calculator-1.0.0.jar filePath=C:\Users\desab\Downloads\saturday.csv
 *
 * Bootstraps and executes the Onboarding Fee Calculator batch job.
 *
 * <p>This is the main entry point of the Spring Boot application.
 * It implements {@link CommandLineRunner} so that after context initialization it can parse the
 * {@code filePath} job parameter and manually launch the batch job through the
 * {@link JobLauncher}.</p>
 *
 * <p><b>Communicates with:</b></p>
 * <ul>
 *   <li>{@link JobLauncher} – to launch the configured batch job</li>
 *   <li>{@link Job} – the <em>onboardingFeeJob</em> bean defined in
 *       {@link com.desabi.guide.spring.batch.feecalculator.config.BatchConfig}</li>
 *   <li>{@link org.springframework.context.ConfigurableApplicationContext} – to
 *       gracefully shut down the Spring context after job completion</li>
 * </ul>
 */
@SpringBootApplication
public class FeeCalculatorApplication implements CommandLineRunner {

  private final JobLauncher jobLauncher;
  private final Job onboardingFeeJob;

  /**
   * Constructor-based dependency injection.
   *
   * @param jobLauncher      the Spring Batch launcher
   * @param onboardingFeeJob the job that defines the read → process → write step
   */
  public FeeCalculatorApplication(JobLauncher jobLauncher, Job onboardingFeeJob) {
    this.jobLauncher = jobLauncher;
    this.onboardingFeeJob = onboardingFeeJob;
  }

  /**
   * Main method that starts the Spring Boot application.
   *
   * @param args command‑line arguments; expected format: {@code filePath=/path/to/file.csv}
   */
  static void main(String[] args) {
    ConfigurableApplicationContext context = SpringApplication.run(FeeCalculatorApplication.class,
        args);
    int exitCode = SpringApplication.exit(context);
    System.exit(exitCode);
  }

	/**
	 * Callback after the application context is loaded.
	 * <p>Extracts the {@code filePath} argument, builds the job parameters,
	 * and triggers the job. If no file path is provided (e.g. during tests),
	 * the job is simply skipped.</p>
	 *
	 * @param args command‑line arguments passed to the application
	 * @throws Exception if job execution fails
	 */
  @Override
  public void run(String... args) throws Exception {
    String filePath = null;
    for (String arg : args) {
      if (arg.startsWith("filePath=")) {
        filePath = arg.substring("filePath=".length());
        break;
      }
    }

    // If no filePath provided (e.g., during tests), simply do nothing.
    if (filePath == null || filePath.isEmpty()) {
      System.out.println(
          "No filePath provided. Skipping job execution. (This is normal during tests)");
      return;
    }

    JobParameters jobParameters = new JobParametersBuilder()
        .addString("filePath", filePath)
        .addLong("time", System.currentTimeMillis())
        .toJobParameters();

    jobLauncher.run(onboardingFeeJob, jobParameters);
  }
}