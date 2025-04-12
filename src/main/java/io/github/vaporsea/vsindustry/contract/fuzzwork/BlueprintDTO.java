package io.github.vaporsea.vsindustry.contract.fuzzwork;

import java.util.List;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Matt Maurer <br>
 * @since 6/17/2024
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BlueprintDTO {
    private int requestedid;
    private long blueprintTypeID;
    private Map<String, List<SkillDTO>> blueprintSkills;
    private BlueprintDetailsDTO blueprintDetails;
    private Map<String, List<MaterialDTO>> activityMaterials;
    private List<DecryptorDTO> decryptors;
}
