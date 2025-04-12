package io.github.vaporsea.vsindustry.domain;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "industry_activity_products")
public class IndustryActivityProduct {
    
    @EmbeddedId
    private IndustryActivityProductKey id;
    private Long quantity;
}
