package com.desabi.guide.spring.batch.postalcodes.domain;

import lombok.Getter;
import lombok.Setter;

/**
 * Represents a row read from the CSV file.
 */
@Setter
@Getter
public class RowRecord {
  private String postalCode;
  private String cityName;
  private String stateName;
}