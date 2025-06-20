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

package io.github.vaporsea.vsindustry.client;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import io.github.vaporsea.vsindustry.domain.Item;
import io.github.vaporsea.vsindustry.domain.ItemRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.ResponseEntity;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import io.github.vaporsea.vsindustry.contract.IndustryJobDTO;
import io.github.vaporsea.vsindustry.contract.JournalEntryDTO;
import io.github.vaporsea.vsindustry.contract.MarketOrderDTO;
import io.github.vaporsea.vsindustry.contract.MarketTransactionDTO;
import io.github.vaporsea.vsindustry.contract.Page;
import io.github.vaporsea.vsindustry.contract.WalletDTO;
import lombok.RequiredArgsConstructor;

/**
 * @author Matt Maurer <br>
 * @since 6/9/2024
 */
@RequiredArgsConstructor
@Component
public class EveClient {
    
    private static final String INDUSTRY_URL = "/latest/corporations/{corporationId}/industry/jobs/";
    private static final String WALLETS_URL = "/latest/corporations/{corporationId}/wallets/";
    private static final String JOURNAL_URL = "/latest/corporations/{corporationId}/wallets/{division}/journal/";
    private static final String MARKET_TRANSACTIONS_URL =
            "/latest/corporations/{corporationId}/wallets/{division}/transactions/";
    private static final String MARKET_ORDERS_URL = "/latest/corporations/{corporationId}/orders/";
    
    private final RestClient restClient;
    private final ItemRepository itemRepository;
    
    @Value("${vsindustry.client.corporationId}")
    private String corporationId;
    
    /**
     * Fetch industry jobs for the corporation
     *
     * @param page The page number to fetch
     *
     * @return A page of industry jobs
     */
    @Cacheable("industryJobs")
    @Retryable
    public Page<IndustryJobDTO> getIndustryJobs(String page) {
        ResponseEntity<List<IndustryJobDTO>> industryJobs = restClient.get()
                                                                      .uri(uriBuilder -> uriBuilder.path(INDUSTRY_URL)
                                                                                                   .queryParam(
                                                                                                           "include_completed",
                                                                                                           "true")
                                                                                                   .queryParam("page",
                                                                                                           page)
                                                                                                   .build(corporationId))
                                                                      .retrieve()
                                                                      .toEntity(new ParameterizedTypeReference<>() {
                                                                      });
        
        return new Page<>(Integer.parseInt(page), Integer.parseInt(industryJobs.getHeaders().get("x-pages").get(0)), 0,
                industryJobs.getBody());
    }
    
    /**
     * Fetch wallet balances for the corporation
     *
     * @return A list of wallets and their balances
     */
    @Cacheable(value = "wallets", key = "'1'")
    @Retryable
    public List<WalletDTO> getWallets() {
        return restClient.get()
                         .uri(uriBuilder -> uriBuilder.path(WALLETS_URL).build(corporationId))
                         .retrieve()
                         .body(new ParameterizedTypeReference<>() {
                         });
    }
    
    /**
     * Fetch journal entries for the corporation
     *
     * @param division The division to fetch journal entries for
     * @param page The page number to fetch
     *
     * @return A page of journal entries
     */
    @Cacheable(value = "journalEntries", key = "#division + #page")
    @Retryable
    public Page<JournalEntryDTO> getJournalEntries(String division, String page) {
        ResponseEntity<List<JournalEntryDTO>> journalEntries = restClient.get()
                                                                         .uri(uriBuilder -> uriBuilder.path(JOURNAL_URL)
                                                                                                      .queryParam(
                                                                                                              "page",
                                                                                                              page)
                                                                                                      .build(corporationId,
                                                                                                              division))
                                                                         .retrieve()
                                                                         .toEntity(new ParameterizedTypeReference<>() {
                                                                         });
        
        return new Page<>(Integer.parseInt(page), Integer.parseInt(journalEntries.getHeaders().get("x-pages").get(0)),
                0, journalEntries.getBody());
    }
    
    /**
     * Fetch market transactions for the corporation
     *
     * @param division The division to fetch market transactions for
     *
     * @return A list of market transactions
     */
    @Cacheable("marketTransactions")
    @Retryable
    public List<MarketTransactionDTO> getMarketTransactions(String division) {
        return restClient.get()
                         .uri(uriBuilder -> uriBuilder.path(MARKET_TRANSACTIONS_URL).build(corporationId, division))
                         .retrieve()
                         .body(new ParameterizedTypeReference<>() {
                         });
    }
    
    /**
     * Fetch market orders for the corporation
     *
     * @return A list of market orders
     */
    @Cacheable(value = "marketOrders", key = "'1'")
    @Retryable
    public List<MarketOrderDTO> getMarketOrders() {
        List<MarketOrderDTO> orders = new ArrayList<>();
        
        ResponseEntity<List<MarketOrderDTO>> marketOrders = restClient.get()
                                                                      .uri(uriBuilder -> uriBuilder.path(
                                                                              MARKET_ORDERS_URL).build(corporationId))
                                                                      .retrieve()
                                                                      .toEntity(new ParameterizedTypeReference<>() {
                                                                      });
        
        orders.addAll(Objects.requireNonNull(marketOrders.getBody()));
        
        int pages = Integer.parseInt(marketOrders.getHeaders().get("x-pages").get(0));
        for (int idx = 2; idx <= pages; idx++) {
            int finalIdx = idx;
            marketOrders = restClient.get()
                                     .uri(uriBuilder -> uriBuilder.path(MARKET_ORDERS_URL)
                                                                  .queryParam("page", Integer.toString(finalIdx))
                                                                  .build(corporationId))
                                     .retrieve()
                                     .toEntity(new ParameterizedTypeReference<>() {
                                     });
            
            orders.addAll(Objects.requireNonNull(marketOrders.getBody()));
        }
        
        orders.forEach(
                order -> order.setTypeName(itemRepository.findById(order.getTypeId()).orElse(new Item()).getName()));
        
        return orders;
    }
}
