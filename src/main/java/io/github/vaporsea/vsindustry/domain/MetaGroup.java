package io.github.vaporsea.vsindustry.domain;

import jakarta.persistence.Column;
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
@Table(name = "inv_meta_groups")
public class MetaGroup {
    
    @Id
    @Column(name = "meta_group_id")
    private Integer metaGroupId;
    
    @Column(name = "meta_group_name")
    private String metaGroupName;
    
    private String description;
    
    @Column(name = "icon_id")
    private Integer iconId;
}
