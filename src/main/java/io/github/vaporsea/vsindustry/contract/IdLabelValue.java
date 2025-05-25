package io.github.vaporsea.vsindustry.contract;

import lombok.Builder;
import lombok.Data;

/**
 * I don't know what to call this so it's called IdLabelValue... because that's what it has... an id... a label... and a
 * value...
 */
@Data
@Builder
public class IdLabelValue {
    
    private String id;
    private String label;
    private String value;
}
