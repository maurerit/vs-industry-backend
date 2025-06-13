package io.github.vaporsea.vsindustry.contract;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MarketPriceDTO implements Serializable {
    @JsonProperty("adjusted_price")
    private Double adjustedPrice;
    
    @JsonProperty("average_price")
    private Double averagePrice;
    
    @JsonProperty("type_id")
    private Long typeId;
}
