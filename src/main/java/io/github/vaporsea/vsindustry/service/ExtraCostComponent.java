package io.github.vaporsea.vsindustry.service;

import io.github.vaporsea.vsindustry.domain.ExtraCost;
import io.github.vaporsea.vsindustry.domain.ExtraCostRepository;
import io.github.vaporsea.vsindustry.domain.Product;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class ExtraCostComponent {
    
    private final ExtraCostRepository extraCostRepository;
    
    public double extraCost(Product productItem) {
        return extraCostRepository.findByItemId(productItem.getItemId()).map(ExtraCost::getCost).orElse(0.0);
    }
}
