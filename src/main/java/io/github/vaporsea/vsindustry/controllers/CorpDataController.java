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
