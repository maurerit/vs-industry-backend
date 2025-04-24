package io.github.vaporsea.vsindustry.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@IdClass(ExtraCostId.class)
@Table(name = "extra_costs")
public class ExtraCost {
    
    @Id
    private Long itemId;
    
    @Id
    private String costType;
    
    private Double cost;
}
