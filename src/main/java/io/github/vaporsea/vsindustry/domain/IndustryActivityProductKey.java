package io.github.vaporsea.vsindustry.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Embeddable
public class IndustryActivityProductKey {
    private Long typeId;
    
    private Long activityId;
    
    private Long productTypeId;
}
