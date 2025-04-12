package io.github.vaporsea.vsindustry.contract;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ActivityMaterialsDTO {
    private List<ActivityMaterialDTO> manufacturing;
    private List<ActivityMaterialDTO> invention;
    private List<ActivityMaterialDTO> copying;
}
