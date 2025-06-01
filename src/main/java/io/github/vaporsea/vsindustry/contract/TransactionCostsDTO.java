package io.github.vaporsea.vsindustry.contract;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class TransactionCostsDTO {
    private Double brokersFee;
    private Double salesTax;
    private List<ExtraCostDTO> extraCosts;
}
