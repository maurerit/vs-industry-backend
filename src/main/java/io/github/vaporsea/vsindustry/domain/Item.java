package io.github.vaporsea.vsindustry.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Matt Maurer <br>
 * @since 6/9/2024
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "inv_types")
public class Item {
    
    @Id
    @Column(name = "type_id")
    private Long itemId;
    
    @Column(name = "type_name")
    private String name;
    
    private String description;
    
    private Long marketGroupId;
}
