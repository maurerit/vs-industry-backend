package io.github.vaporsea.vsindustry.util;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import lombok.Getter;

/**
 * Enum representing the major trade hubs in EVE Online.
 */
@Getter
public enum TradeHub {
    JITA(10000002L, 30000142L),
    AMARR(10000043L, 30002187L),
    RENS(10000030L, 30002510L),
    HEK(10000042L, 30002053L),
    DODIXIE(10000032L, 30002659L);

    private static final Map<Long, TradeHub> SYSTEM_ID_MAP = Arrays.stream(values())
            .collect(Collectors.toMap(TradeHub::getSystemId, Function.identity()));

    private final Long regionId;
    private final Long systemId;

    TradeHub(Long regionId, Long systemId) {
        this.regionId = regionId;
        this.systemId = systemId;
    }

    /**
     * Get a TradeHub by its system ID.
     *
     * @param systemId the system ID to look up
     * @return the TradeHub for the given system ID, or null if not found
     */
    public static TradeHub fromSystemId(Long systemId) {
        return SYSTEM_ID_MAP.get(systemId);
    }
    
    public static TradeHub fromName(String name) {
        return Arrays.stream(values())
                     .filter(tradeHub -> tradeHub.name().equalsIgnoreCase(name))
                     .findFirst()
                     .orElse(null);
    }
}
