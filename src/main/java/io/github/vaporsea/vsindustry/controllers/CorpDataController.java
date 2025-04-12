package io.github.vaporsea.vsindustry.controllers;

import io.github.vaporsea.vsindustry.client.EveClient;
import io.github.vaporsea.vsindustry.contract.IndustryJobDTO;
import io.github.vaporsea.vsindustry.contract.JournalEntryDTO;
import io.github.vaporsea.vsindustry.contract.MarketOrderDTO;
import io.github.vaporsea.vsindustry.contract.MarketTransactionDTO;
import io.github.vaporsea.vsindustry.contract.WalletDTO;
import io.github.vaporsea.vsindustry.domain.IndustryJobRepository;
import io.github.vaporsea.vsindustry.domain.JournalEntryRepository;
import io.github.vaporsea.vsindustry.domain.MarketTransactionRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/corp")
public class CorpDataController {
    
    private final MarketTransactionRepository marketTransactionRepository;
    private final IndustryJobRepository industryJobRepository;
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
    public List<IndustryJobDTO> getIndustryJobs() {
        return industryJobRepository.findAll()
                                    .stream()
                                    .map(model -> modelMapper.map(model, IndustryJobDTO.class))
                                    .toList();
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
