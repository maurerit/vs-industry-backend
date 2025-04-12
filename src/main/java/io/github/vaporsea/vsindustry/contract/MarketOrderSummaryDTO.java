package io.github.vaporsea.vsindustry.contract;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Map;

public record MarketOrderSummaryDTO(Map<Integer, Integer> totalOrders, Map<Integer, BigDecimal> totalSell,
                                    Map<Integer, BigDecimal> totalBuy) implements Serializable {
    
}
