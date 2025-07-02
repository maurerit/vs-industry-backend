package io.github.vaporsea.vsindustry.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExtraCostId implements Serializable {
    
    private Long itemId;
    
    private String costType;
}

