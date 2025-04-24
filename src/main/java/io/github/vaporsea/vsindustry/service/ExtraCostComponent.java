package io.github.vaporsea.vsindustry.service;

import io.github.vaporsea.vsindustry.domain.ExtraCost;
import io.github.vaporsea.vsindustry.domain.ExtraCostRepository;
import io.github.vaporsea.vsindustry.domain.Product;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@RequiredArgsConstructor
@Component
public class ExtraCostComponent {
    
    private final ExtraCostRepository extraCostRepository;
    
    public double extraCost(Product productItem) {
        List<ExtraCost> extraCosts = extraCostRepository.findByItemId(productItem.getItemId());
        if (extraCosts.isEmpty()) {
            return 0.0;
        }
        
        return extraCosts.stream()
                         .mapToDouble(ExtraCost::getCost)
                         .sum();
    }
}
