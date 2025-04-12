package io.github.vaporsea.vsindustry.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
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
@Table(name = "inv_types")
public class Type {
    @Id
    private Long typeId;
    
    private Long groupId;
    
    private String typeName;
    
    private String description;
    
    private Double mass;
    
    private Double volume;
    
    private Double capacity;
    
    private Long portionSize;
    
    private Long raceId;
    
    private Double basePrice;
    
    private Integer published;
    
    private Long marketGroupId;
    
    private Long iconId;
    
    private Long soundId;
    
    private Long graphicId;
}
