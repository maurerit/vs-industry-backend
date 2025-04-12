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
public class ContractDetailDTO implements Serializable {
    
    @JsonProperty("is_included")
    private Boolean isIncluded;
    
    @JsonProperty("is_singleton")
    private Boolean isSingleton;
    
    @JsonProperty("quantity")
    private Integer quantity;
    
    @JsonProperty("raw_quantity")
    private Integer rawQuantity;
    
    @JsonProperty("record_id")
    private Long recordId;
    
    @JsonProperty("type_id")
    private Long typeId;
}
