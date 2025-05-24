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

package io.github.vaporsea.vsindustry.controllers;

import io.github.vaporsea.vsindustry.contract.MarketOrderSummaryDTO;
import io.github.vaporsea.vsindustry.service.DataSyncScheduler;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import io.github.vaporsea.vsindustry.service.DataFetchService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;
import java.util.Optional;

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
    
    /**
     * Endpoint to store JWT and refresh tokens from cookies. Extracts EVEJWT and EVERefresh cookies from the request
     * and stores them in the database.
     *
     * @param request the HTTP request containing the cookies
     *
     * @return a response entity indicating success or failure
     */
    @PostMapping("/data/store-tokens")
    public ResponseEntity<String> storeTokens(HttpServletRequest request) {
        if (request.getCookies() == null) {
            log.warn("No cookies found in request");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("{ \"error\": \"No cookies found in request\" }");
        }
        
        Optional<Cookie> jwtCookie = Arrays.stream(request.getCookies())
                                           .filter(cookie -> "EVEJWT".equals(cookie.getName()))
                                           .findFirst();
        
        Optional<Cookie> refreshCookie = Arrays.stream(request.getCookies())
                                               .filter(cookie -> "EVERefresh".equals(cookie.getName()))
                                               .findFirst();
        
        Optional<Cookie> expiryTimeCookie = Arrays.stream(request.getCookies())
                                                  .filter(cookie -> "EVETokenExpiry".equals(cookie.getName()))
                                                  .findFirst();
        
        if (jwtCookie.isEmpty() || refreshCookie.isEmpty()) {
            log.warn("Required cookies not found. EVEJWT: {}, EVERefresh: {}",
                    jwtCookie.isPresent(), refreshCookie.isPresent());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                                 .body("{ \"error\": \"Required cookies not found\" }");
        }
        
        try {
            String expiryTime = expiryTimeCookie.map(Cookie::getValue).orElse(null);
            dataFetchService.storeTokens(jwtCookie.get().getValue(), refreshCookie.get().getValue(), expiryTime);
            return ResponseEntity.ok("{ \"status\": \"Tokens stored successfully\" }");
        }
        catch (Exception e) {
            log.error("Error storing tokens", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                 .body("{ \"error\": \"Failed to store tokens: " + e.getMessage() + "\" }");
        }
    }
}
