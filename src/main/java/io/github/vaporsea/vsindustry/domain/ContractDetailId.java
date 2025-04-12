package io.github.vaporsea.vsindustry.domain;

import java.io.Serializable;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ContractDetailId implements Serializable {
    private Long contractId;
    private Long recordId;
}