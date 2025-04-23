package io.github.vaporsea.vsindustry.controllers;

import io.github.vaporsea.vsindustry.contract.MarketOrderSummaryDTO;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import io.github.vaporsea.vsindustry.service.DataFetchService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author Matt Maurer <br>
 * @since 6/8/2024
 */
@Slf4j
@RequiredArgsConstructor
@RestController
public class DataFetchController {
    
    private final AtomicBoolean isFetching = new AtomicBoolean(false);
    
    private final DataFetchService dataFetchService;
    
    @PostMapping("/data/fetch-all")
    public void fetch() {
        if (isFetching.get()) {
            log.warn("Data fetch already in progress");
            return;
        }
        
        isFetching.set(true);
        dataFetchService.fetchMarketTransactions();
        dataFetchService.fetchJournalEntries();
        dataFetchService.fetchIndustryJobs();
        dataFetchService.fetchContracts();
        isFetching.set(false);
    }
    
    @GetMapping("/data/fetch-status")
    public String fetchStatus() {
        return isFetching.get() ? "{ \"status\": \"Fetching in progress\" }" :
                "{ \"status\": \"No fetch in progress\" }";
    }
    
    @GetMapping("/data/market-order-summary")
    public MarketOrderSummaryDTO getMarketOrderSummary() {
        return dataFetchService.getMarketOrderSummary();
    }
}
