package io.github.vaporsea.vsindustry.domain;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ContractDetailRepository extends JpaRepository<ContractDetail, ContractDetailId> {
    
    List<ContractDetail> findByContractId(Long contractId);
}
