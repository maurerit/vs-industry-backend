package io.github.vaporsea.vsindustry.domain;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ExtraCostRepository extends JpaRepository<ExtraCost, ExtraCostId> {
    
    List<ExtraCost> findByItemId(Long itemId);
    
    Optional<ExtraCost> findByItemIdAndCostType(Long itemId, String costType);
}
