package io.github.vaporsea.vsindustry.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "market_prices")
public class MarketPrice {
    @Id
    private Long typeId;
    
    private Double adjustedPrice;
    
    private Double averagePrice;
}
