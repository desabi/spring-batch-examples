package com.desabi.guide.spring.batch.postalcodes.repository;

import com.desabi.guide.spring.batch.postalcodes.domain.PostalCodeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

/**
 * Repository for accessing postal code data.
 */
public interface PostalCodeRepository extends JpaRepository<PostalCodeEntity, Long> {
    Optional<PostalCodeEntity> findByPostalCode(String postalCode);
}