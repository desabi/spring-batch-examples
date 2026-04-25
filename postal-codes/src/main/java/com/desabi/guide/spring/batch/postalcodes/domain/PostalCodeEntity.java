package com.desabi.guide.spring.batch.postalcodes.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

/**
 * Entity mapped to the database table postal_codes.
 */
@Entity
@Table(name = "postal_codes")
@Setter
@Getter
public class PostalCodeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String postalCode;

    @Column(nullable = false)
    private String cityName;

    @Column(nullable = false)
    private String stateName;

    // Getters and setters
}