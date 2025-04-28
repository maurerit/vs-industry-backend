package io.github.vaporsea.vsindustry.controllers;

import io.github.vaporsea.vsindustry.client.EveClient;
import io.github.vaporsea.vsindustry.contract.IndustryJobDTO;
import io.github.vaporsea.vsindustry.contract.JournalEntryDTO;
import io.github.vaporsea.vsindustry.contract.MarketOrderDTO;
import io.github.vaporsea.vsindustry.contract.MarketTransactionDTO;
import io.github.vaporsea.vsindustry.contract.WalletDTO;
import io.github.vaporsea.vsindustry.domain.JournalEntryRepository;
import io.github.vaporsea.vsindustry.domain.MarketTransactionRepository;
import io.github.vaporsea.vsindustry.service.CorpDataService;
import io.github.vaporsea.vsindustry.service.IndustryJobStatusSearch;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/corp")
public class CorpDataController {
    
    private final CorpDataService corpDataService;
    private final MarketTransactionRepository marketTransactionRepository;
    private final JournalEntryRepository journalEntryRepository;
    private final EveClient eveClient;
    private final ModelMapper modelMapper;
    
    @GetMapping("/market-transactions")
    public List<MarketTransactionDTO> getMarketTransactions() {
        return marketTransactionRepository.findAll()
                                          .stream()
                                          .map(model -> modelMapper.map(model, MarketTransactionDTO.class))
                                          .toList();
    }
    
    @GetMapping("/wallet-journal")
    public List<JournalEntryDTO> getJournalEntries() {
        return journalEntryRepository.findAll()
                                     .stream()
                                     .map(model -> modelMapper.map(model, JournalEntryDTO.class))
                                     .toList();
    }
    
    @GetMapping("/industry-jobs")
    public Page<IndustryJobDTO> getIndustryJobs(IndustryJobStatusSearch search, Pageable pageable) {
        return corpDataService.getIndustryJobs(search, pageable);
    }
    
    @GetMapping("/market-orders")
    public List<MarketOrderDTO> getMarketOrders() {
        return eveClient.getMarketOrders();
    }
    
    @GetMapping("/wallet-balances")
    public List<WalletDTO> getWalletBalances() {
        return eveClient.getWallets();
    }
}
