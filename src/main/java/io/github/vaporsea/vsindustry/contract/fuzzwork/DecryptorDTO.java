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
public class DecryptorDTO {
    private String name;
    private int typeid;
    private double multiplier;
    private int me;
    private int te;
    private int runs;
}
