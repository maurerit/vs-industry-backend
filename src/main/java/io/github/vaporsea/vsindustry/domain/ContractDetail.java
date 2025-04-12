package io.github.vaporsea.vsindustry.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.IdClass;
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
@Table(name = "contract_details")
@IdClass(ContractDetailId.class)
public class ContractDetail {
    
    @Id
    private Long contractId;
    
    @Id
    private Long recordId;
    
    private Long typeId;
    private Boolean isIncluded;
    private Boolean isSingleton;
    private Integer quantity;
    private Integer rawQuantity;
}