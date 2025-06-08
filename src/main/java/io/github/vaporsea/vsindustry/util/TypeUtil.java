package io.github.vaporsea.vsindustry.util;

import io.github.vaporsea.vsindustry.domain.Item;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class TypeUtil {
    
    public static final Map<String, Integer> TECH_LEVEL_MAP = Map.of(
            "Tech I", 1,
            "Tech II", 2,
            "Tech III", 3
    );
    
    public static int techLevel(Item item) {
        int techLevel = 1;
        if(item.getMetaType() != null) {
            //Account for tech 2 and tech 3 items
            techLevel = TECH_LEVEL_MAP.getOrDefault(item.getMetaType().getMetaGroup().getMetaGroupName(), 1);
        }
        
        return techLevel;
    }
}
