package io.github.vaporsea.vsindustry.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import io.github.vaporsea.vsindustry.client.EveContractClient;
import io.github.vaporsea.vsindustry.contract.*;
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
                                  JournalEntry journalEntry = modelMapper.map(entry, JournalEntry.class);
                                  journalEntry.setDivisionId(finalDivision);
                                  journalEntryRepository.save(journalEntry);
                              });
            }
        }
    }
    
    public void fetchMarketTransactions() {
        for (int division = 1; division <= 5; division++) {
            List<MarketTransactionDTO> marketTransactions = eveClient.getMarketTransactions(String.valueOf(division));
            
            int finalDivision = division;
            marketTransactions.forEach(transaction -> {
                MarketTransaction marketTransaction = modelMapper.map(transaction, MarketTransaction.class);
                marketTransaction.setDivisionId(finalDivision);
                marketTransactionRepository.save(marketTransaction);
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
}
