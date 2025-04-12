package io.github.vaporsea.vsindustry.contract;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BlueprintDetailsDTO implements Serializable {
    
    private Long maxProductionLimit;
    private Long productTypeID;
    private String productTypeName;
    private Long productQuantity;
    private Long productMakeTypeID;
}
