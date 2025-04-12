package io.github.vaporsea.vsindustry.contract.fuzzwork;

import java.util.List;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Matt Maurer <br>
 * @since 6/17/2024
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BlueprintDetailsDTO {
    private int maxProductionLimit;
    private long productTypeID;
    private String productTypeName;
    private long productQuantity;
    private Map<String, Integer> times;
    private List<String> facilities;
    private int techLevel;
    private double adjustedPrice;
    private double precursorAdjustedPrice;
    private int precursorTypeId;
    private double probability;
}
