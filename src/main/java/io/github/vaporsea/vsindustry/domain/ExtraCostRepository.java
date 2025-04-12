package io.github.vaporsea.vsindustry.domain;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ExtraCostRepository extends JpaRepository<ExtraCost, Long> {
    
    Optional<ExtraCost> findByItemId(Long itemId);
}
