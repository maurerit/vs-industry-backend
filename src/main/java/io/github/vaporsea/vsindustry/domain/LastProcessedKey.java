package io.github.vaporsea.vsindustry.domain;

import java.io.Serializable;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Matt Maurer <br>
 * @since 6/14/2024
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Embeddable
public class LastProcessedKey implements Serializable {
    private Long objectId;
    private String objectType;
}
