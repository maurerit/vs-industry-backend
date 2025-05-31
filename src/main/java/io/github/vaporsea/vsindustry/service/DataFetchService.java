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

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.vaporsea.vsindustry.client.EveContractClient;
import io.github.vaporsea.vsindustry.contract.*;
import io.github.vaporsea.vsindustry.util.JwtTokenUtil;
import io.github.vaporsea.vsindustry.domain.AuthToken;
import io.github.vaporsea.vsindustry.domain.AuthTokenRepository;
import io.github.vaporsea.vsindustry.domain.ContractDetail;
import io.github.vaporsea.vsindustry.domain.ContractDetailRepository;
import io.github.vaporsea.vsindustry.domain.ContractHeader;
import io.github.vaporsea.vsindustry.domain.ContractHeaderRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import io.github.vaporsea.vsindustry.client.EveClient;
import io.github.vaporsea.vsindustry.domain.IndustryJob;
import io.github.vaporsea.vsindustry.domain.IndustryJobRepository;
import io.github.vaporsea.vsindustry.domain.JournalEntry;
import io.github.vaporsea.vsindustry.domain.JournalEntryRepository;
import io.github.vaporsea.vsindustry.domain.MarketTransaction;
import io.github.vaporsea.vsindustry.domain.MarketTransactionRepository;
import lombok.RequiredArgsConstructor;

/**
 * @author Matt Maurer <br>
 * @since 6/9/2024
 */
@RequiredArgsConstructor
@Service
public class DataFetchService {

    private final EveClient eveClient;
    private final EveContractClient eveContractClient;
    private final IndustryJobRepository industryJobRepository;
    private final JournalEntryRepository journalEntryRepository;
    private final MarketTransactionRepository marketTransactionRepository;
    private final ContractHeaderRepository contractHeaderRepository;
    private final ContractDetailRepository contractDetailRepository;
    private final AuthTokenRepository authTokenRepository;
    private final JwtTokenUtil jwtTokenUtil;
    private final ModelMapper modelMapper = new ModelMapper();

    @Value("${vsindustry.client.corporationId}")
    private String corporationId;

    public void fetchIndustryJobs() {
        Page<IndustryJobDTO> industryJobs = eveClient.getIndustryJobs("1");

        industryJobs.content().forEach(job -> industryJobRepository.save(modelMapper.map(job, IndustryJob.class)));

        for (int idx = 2; idx <= industryJobs.totalPages(); idx++) {
            industryJobs = eveClient.getIndustryJobs(String.valueOf(idx));

            industryJobs.content().forEach(job -> industryJobRepository.save(modelMapper.map(job, IndustryJob.class)));
        }
    }

    public void fetchJournalEntries() {
        for (int division = 1; division <= 5; division++) {
            Page<JournalEntryDTO> journalEntries = eveClient.getJournalEntries(String.valueOf(division), "1");

            int finalDivision = division;
            journalEntries.content().forEach(entry -> {
                JournalEntry journalEntry = modelMapper.map(entry, JournalEntry.class);
                journalEntry.setDivisionId(finalDivision);
                journalEntryRepository.save(journalEntry);
            });

            for (int idx = 2; idx <= journalEntries.totalPages(); idx++) {
                journalEntries = eveClient.getJournalEntries(String.valueOf(division), String.valueOf(idx));

                journalEntries.content()
                              .forEach(entry -> {
                                  if(!journalEntryRepository.existsById(entry.getId())) {
                                      JournalEntry journalEntry = modelMapper.map(entry, JournalEntry.class);
                                      journalEntry.setDivisionId(finalDivision);
                                      journalEntryRepository.save(journalEntry);
                                  }
                              });
            }
        }
    }

    public void fetchMarketTransactions() {
        for (int division = 1; division <= 5; division++) {
            List<MarketTransactionDTO> marketTransactions = eveClient.getMarketTransactions(String.valueOf(division));

            int finalDivision = division;
            marketTransactions.forEach(transaction -> {
                if(!marketTransactionRepository.existsById(transaction.getTransactionId())) {
                    MarketTransaction marketTransaction = modelMapper.map(transaction, MarketTransaction.class);
                    marketTransaction.setDivisionId(finalDivision);
                    marketTransactionRepository.save(marketTransaction);
                }
            });
        }
    }

    public void fetchContracts() {
        Page<ContractHeaderDTO> contractHeaders = eveContractClient.getContracts(corporationId, "1");

        fetchAndSaveContracts(contractHeaders);

        for (int idx = 2; idx <= contractHeaders.totalPages(); idx++) {
            contractHeaders = eveContractClient.getContracts(corporationId, String.valueOf(idx));
            fetchAndSaveContracts(contractHeaders);
        }
    }

    private void fetchAndSaveContracts(Page<ContractHeaderDTO> contractHeaders) {
        contractHeaders.content().forEach(header -> {
            contractHeaderRepository.save(modelMapper.map(header, ContractHeader.class));

            List<ContractDetailDTO> contractDetails =
                    eveContractClient.getContractDetails(corporationId, String.valueOf(header.getContractId()));
            contractDetails.forEach(detail -> {
                //Not using model mapper here because I keep getting this error:
                /*
                1) The destination property io.github.vaporsea.vsindustry.domain.ContractDetail.setContractId()
                matches multiple source property hierarchies:

                    io.github.vaporsea.vsindustry.contract.ContractDetailDTO.getTypeId()
                    io.github.vaporsea.vsindustry.contract.ContractDetailDTO.getRecordId()

                1 error
                 */
                contractDetailRepository.save(ContractDetail.builder()
                                                            .contractId(header.getContractId())
                                                            .isIncluded(detail.getIsIncluded())
                                                            .isSingleton(detail.getIsSingleton())
                                                            .quantity(detail.getQuantity())
                                                            .rawQuantity(detail.getRawQuantity())
                                                            .recordId(detail.getRecordId())
                                                            .typeId(detail.getTypeId())
                                                            .build());
            });
        });
    }

    public MarketOrderSummaryDTO getMarketOrderSummary() {
        List<MarketOrderDTO> marketOrders = eveClient.getMarketOrders();

        Map<Integer, BigDecimal> sellOrders = marketOrders.stream()
                                                          .filter(order -> !order.isBuyOrder())
                                                          .collect(Collectors.groupingBy(MarketOrderDTO::getDivisionId,
                                                                  Collectors.reducing(BigDecimal.ZERO,
                                                                          order -> BigDecimal.valueOf(
                                                                                  order.getPrice() *
                                                                                          order.getVolumeRemain()),
                                                                          BigDecimal::add)));

        Map<Integer, BigDecimal> buyOrders = marketOrders.stream()
                                                         .filter(MarketOrderDTO::isBuyOrder)
                                                         .collect(Collectors.groupingBy(MarketOrderDTO::getDivisionId,
                                                                 Collectors.reducing(BigDecimal.ZERO,
                                                                         order -> BigDecimal.valueOf(
                                                                                 order.getPrice() *
                                                                                         order.getVolumeRemain()),
                                                                         BigDecimal::add)));

        Map<Integer, Integer> totalOrders = marketOrders.stream()
                                                        .collect(Collectors.groupingBy(MarketOrderDTO::getDivisionId,
                                                                Collectors.summingInt(order -> 1)));

        return new MarketOrderSummaryDTO(totalOrders, sellOrders, buyOrders);
    }

    /**
     * Stores the JWT token and refresh token in the auth_tokens table.
     * Extracts the name from the JWT token and uses it as the principal.
     *
     * @param jwtToken the JWT token
     * @param refreshToken the refresh token
     * @param expiryTimeStr the token expiry time in ISO-8601 format (e.g., 2025-04-25T02:01:16.369Z)
     */
    public void storeTokens(String jwtToken, String refreshToken, String expiryTimeStr) {
        try {
            String name = jwtTokenUtil.getUsername(jwtToken);

            ZonedDateTime expiresAt;
            if (expiryTimeStr != null && !expiryTimeStr.isEmpty()) {
                // Parse the expiry time from the cookie value
                expiresAt = ZonedDateTime.parse(expiryTimeStr);
            } else {
                // Fallback to parsing from JWT token if no expiry time cookie is provided
                String tokenBody = jwtToken.split("\\.")[1];
                String decodedBody = new String(java.util.Base64.getDecoder().decode(tokenBody));
                EveJwtDTO eveJwtDTO = new ObjectMapper().readValue(decodedBody, EveJwtDTO.class);

                // Convert epoch seconds to ZonedDateTime
                expiresAt = ZonedDateTime.now().plusSeconds(eveJwtDTO.getExp() - (System.currentTimeMillis() / 1000));
            }

            ZonedDateTime createdAt = ZonedDateTime.now();

            AuthToken authToken = AuthToken.builder()
                    .principal(name)
                    .token(jwtToken)
                    .refreshToken(refreshToken)
                    .createdAt(createdAt)
                    .expiresAt(expiresAt)
                    .build();

            authTokenRepository.save(authToken);
        } catch (Exception e) {
            throw new RuntimeException("Failed to store tokens", e);
        }
    }
}
