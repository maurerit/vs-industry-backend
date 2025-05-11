/*
 * MIT License
 *
 * Copyright (c) 2025 VaporSea
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

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
     * Scheduled task to fetch all data every 30 minutes. The initial delay is set to 1 minute to allow the application
     * to start up.
     */
    @Scheduled(fixedRate = 2_100_000, initialDelay = 60_000)
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
        }
        catch (Exception e) {
            log.error("Error during scheduled data fetch", e);
        }
        finally {
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