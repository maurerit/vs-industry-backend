package io.github.vaporsea.vsindustry.controllers;

import io.github.vaporsea.vsindustry.contract.MarketOrderSummaryDTO;
import io.github.vaporsea.vsindustry.service.DataFetchService;
import io.github.vaporsea.vsindustry.service.DataSyncScheduler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Matt Maurer <br>
 * @since 6/8/2024
 */
@Slf4j
@RequiredArgsConstructor
@RestController
public class DataFetchController {
    
    private final DataFetchService dataFetchService;
    private final DataSyncScheduler dataSyncScheduler;
    
    @PostMapping("/data/fetch-all")
    public void fetch() {
        if (dataSyncScheduler.isFetching()) {
            log.warn("Data fetch already in progress");
            return;
        }
        
        dataSyncScheduler.setFetching(true);
        dataFetchService.fetchMarketTransactions();
        dataFetchService.fetchJournalEntries();
        dataFetchService.fetchIndustryJobs();
        dataFetchService.fetchContracts();
        dataSyncScheduler.setFetching(false);
    }
    
    @GetMapping("/data/fetch-status")
    public String fetchStatus() {
        return dataSyncScheduler.isFetching() ? "{ \"status\": \"Fetching in progress\" }" :
                "{ \"status\": \"No fetch in progress\" }";
    }
    
    @GetMapping("/data/market-order-summary")
    public MarketOrderSummaryDTO getMarketOrderSummary() {
        return dataFetchService.getMarketOrderSummary();
    }
}
