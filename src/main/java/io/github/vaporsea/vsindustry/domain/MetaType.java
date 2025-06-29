package io.github.vaporsea.vsindustry.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
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
@Table(name = "inv_meta_types")
public class MetaType {

    @Id
    @Column(name = "type_id")
    private Long typeId;

    @Column(name = "parent_type_id")
    private Integer parentTypeId;

    @Column(name = "meta_group_id")
    private Integer metaGroupId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "meta_group_id", insertable = false, updatable = false)
    private MetaGroup metaGroup;
}
