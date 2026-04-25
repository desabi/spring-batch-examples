package com.desabi.guide.spring.batch.postalcodes.writer;

import com.desabi.guide.spring.batch.postalcodes.domain.*;
import com.desabi.guide.spring.batch.postalcodes.repository.PostalCodeRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;

import java.util.Optional;

/**
 * Writes processed records into the database.
 *
 * <p>This writer handles three operation types:
 * <ul>
 *     <li>INSERT - creates new records</li>
 *     <li>UPDATE - modifies existing records</li>
 *     <li>SKIP - ignores the record</li>
 * </ul>
 *
 * <p>Uses chunk-based processing introduced in Spring Batch 5.</p>
 */
@Slf4j
public class PostalCodeWriter implements ItemWriter<ProcessedRecord> {

  private final PostalCodeRepository repository;

  public PostalCodeWriter(PostalCodeRepository repository) {
    this.repository = repository;
  }

  /**
   * Writes a chunk of processed records into the database.
   *
   * @param chunk a batch (chunk) of processed records
   */
  @Override
  public void write(Chunk<? extends ProcessedRecord> chunk) {

    for (ProcessedRecord pr : chunk) {
      RowRecord record = pr.getRecord();
      log.info("Writing postal code record: {}", record);
      switch (pr.getOperationType()) {
        case INSERT -> handleInsert(record);
        case UPDATE -> handleUpdate(record);
        case SKIP -> {
          // Intentionally ignored
        }
      }
    }
  }

  /**
   * Handles insert operation.
   */
  private void handleInsert(RowRecord record) {
    PostalCodeEntity entity = new PostalCodeEntity();
    entity.setPostalCode(record.getPostalCode());
    entity.setCityName(record.getCityName());
    entity.setStateName(record.getStateName());

    repository.save(entity);
  }

  /**
   * Handles update operation.
   */
  private void handleUpdate(RowRecord record) {
    Optional<PostalCodeEntity> optional = repository.findByPostalCode(record.getPostalCode());

    if (optional.isPresent()) {
      PostalCodeEntity entity = optional.get();
      entity.setCityName(record.getCityName());
      entity.setStateName(record.getStateName());

      repository.save(entity);
    }
    // Defensive: if not found, we avoid crashing the job
  }
}