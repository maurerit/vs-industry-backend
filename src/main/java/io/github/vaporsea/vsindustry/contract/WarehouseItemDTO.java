package io.github.vaporsea.vsindustry.contract;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WarehouseItemDTO {
    private Long itemId;
    
    private String name;
    
    private Long quantity;
    
    private Double costPerItem;
}
