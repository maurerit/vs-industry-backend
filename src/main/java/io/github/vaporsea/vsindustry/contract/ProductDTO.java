package io.github.vaporsea.vsindustry.contract;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ProductDTO {
    
    private Long itemId;
    
    private String name;
    
    private Double cost;
    
    private String description;
}
