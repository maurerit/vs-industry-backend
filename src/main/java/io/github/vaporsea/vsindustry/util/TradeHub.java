package io.github.vaporsea.vsindustry.util;

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

    private final Long regionId;
    private final Long systemId;

    TradeHub(Long regionId, Long systemId) {
        this.regionId = regionId;
        this.systemId = systemId;
    }
}
