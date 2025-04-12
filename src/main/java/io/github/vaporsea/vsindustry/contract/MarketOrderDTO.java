package io.github.vaporsea.vsindustry.contract;

import java.io.Serializable;
import java.time.ZonedDateTime;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Matt Maurer <br>
 * @since 6/9/2024
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MarketOrderDTO implements Serializable {
    
    @JsonProperty("order_id")
    private Long orderId;
    
    @JsonProperty("duration")
    private Integer duration;
    
    @JsonProperty("issued")
    private ZonedDateTime issued;
    
    @JsonProperty("price")
    private Double price;
    
    @JsonProperty("type_id")
    private Long typeId;
    
    @JsonProperty("volume_remain")
    private Integer volumeRemain;
    
    @JsonProperty("volume_total")
    private Integer volumeTotal;
    
    @JsonProperty("wallet_division")
    private Integer divisionId;
    
    @JsonProperty("is_buy_order")
    private boolean buyOrder;
}
