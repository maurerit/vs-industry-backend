package io.github.vaporsea.vsindustry.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExtraCostId implements Serializable {
    
    private Long itemId;
    
    private String costType;
}

