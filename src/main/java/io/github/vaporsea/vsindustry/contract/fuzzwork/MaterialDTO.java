package io.github.vaporsea.vsindustry.contract.fuzzwork;

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
public class MaterialDTO {
    private long typeid;
    private String name;
    private long quantity;
    private long maketype;
}
