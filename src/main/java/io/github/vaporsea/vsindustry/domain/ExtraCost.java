package io.github.vaporsea.vsindustry.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "extra_costs")
public class ExtraCost {
    
    @Id
    private Long itemId;
    private Double cost;
}
