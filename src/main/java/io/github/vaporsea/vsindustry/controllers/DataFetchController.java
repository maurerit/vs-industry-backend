package io.github.vaporsea.vsindustry.controllers;

import io.github.vaporsea.vsindustry.contract.MarketOrderSummaryDTO;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import io.github.vaporsea.vsindustry.service.DataFetchService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Matt Maurer <br>
 * @since 6/8/2024
 */
@Slf4j
@RequiredArgsConstructor
@RestController
public class DataFetchController {
    
    private final DataFetchService dataFetchService;
    
    @PostMapping("/data/fetch-all")
    public void fetch() {
        dataFetchService.fetchMarketTransactions();
        dataFetchService.fetchJournalEntries();
        dataFetchService.fetchIndustryJobs();
        dataFetchService.fetchContracts();
    }
    
    @GetMapping("/data/market-order-summary")
    public MarketOrderSummaryDTO getMarketOrderSummary() {
        return dataFetchService.getMarketOrderSummary();
    }
}
