package io.github.vaporsea.vsindustry.domain;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @author Matt Maurer <br>
 * @since 6/14/2024
 */
@Repository
public interface LastProcessedRepository extends JpaRepository<LastProcessed, LastProcessedKey> {
    LastProcessed findById_ObjectType(String objectType);

    Optional<LastProcessed> findById_ObjectId(Long objectId);
}
