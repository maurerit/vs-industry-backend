package io.github.vaporsea.vsindustry.contract;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BlueprintDataDTO implements Serializable {
    private BlueprintDetailsDTO blueprintDetails;
    private ActivityMaterialsDTO activityMaterials;
}
