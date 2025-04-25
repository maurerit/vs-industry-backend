package io.github.vaporsea.vsindustry.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Service responsible for scheduling and executing data sync operations.
 * 
 * @author Matt Maurer
 * @since 6/17/2024
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DataSyncScheduler {

    private final DataFetchService dataFetchService;
    private final AtomicBoolean isFetching = new AtomicBoolean(false);

    /**
     * Scheduled task to fetch all data every 30 minutes.
     * The initial delay is set to 1 minute to allow the application to start up.
     */
    @Scheduled(fixedRate = 1800000, initialDelay = 60000)
    public void scheduledDataFetch() {
        if (isFetching.get()) {
            log.warn("Data fetch already in progress, skipping scheduled fetch");
            return;
        }

        try {
            isFetching.set(true);
            log.info("Starting scheduled data fetch");
            dataFetchService.fetchMarketTransactions();
            dataFetchService.fetchJournalEntries();
            dataFetchService.fetchIndustryJobs();
            dataFetchService.fetchContracts();
            log.info("Completed scheduled data fetch");
        } catch (Exception e) {
            log.error("Error during scheduled data fetch", e);
        } finally {
            isFetching.set(false);
        }
    }

    /**
     * Get the current status of the data fetch operation.
     * 
     * @return true if a fetch is in progress, false otherwise
     */
    public boolean isFetching() {
        return isFetching.get();
    }

    public void setFetching(boolean fetching) {
        isFetching.set(fetching);
    }
} 