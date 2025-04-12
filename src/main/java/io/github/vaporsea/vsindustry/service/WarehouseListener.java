package io.github.vaporsea.vsindustry.service;

import io.github.vaporsea.vsindustry.contract.ContractHeaderDTO;
import io.github.vaporsea.vsindustry.contract.IndustryJobDTO;
import io.github.vaporsea.vsindustry.contract.MarketTransactionDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class WarehouseListener {
    
    public void marketTransactionProcessed(MarketTransactionDTO marketTransaction) {
        log.debug("Market transaction processed {}", marketTransaction);
    }
    
    public void industryJobProcessed(IndustryJobDTO industryJob) {
        log.debug("Industry job processed {}", industryJob);
    }
    
    public void contractProcessed(ContractHeaderDTO contractHeader) {
        log.debug("Contract processed {}", contractHeader);
    }
}
