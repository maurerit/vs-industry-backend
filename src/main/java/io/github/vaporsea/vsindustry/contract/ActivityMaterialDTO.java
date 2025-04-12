package io.github.vaporsea.vsindustry.contract;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ActivityMaterialDTO {
    
    Long typeid;
    String name;
    Long quantity;
    Long maketype;
    Double price;
}
