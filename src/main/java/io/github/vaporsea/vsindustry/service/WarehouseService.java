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

import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import io.github.vaporsea.vsindustry.component.Warehouse;
import io.github.vaporsea.vsindustry.contract.ContractHeaderDTO;
import io.github.vaporsea.vsindustry.contract.WarehouseItemDTO;
import io.github.vaporsea.vsindustry.domain.*;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.github.vaporsea.vsindustry.contract.IndustryJobDTO;
import io.github.vaporsea.vsindustry.contract.MarketTransactionDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Matt Maurer <br>
 * @since 6/9/2024
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class WarehouseService {
    
    private static final String MARKET_TRANSACTION_NAME = "market_transaction";
    private static final String INDUSTRY_JOB_NAME = "industry_job";
    
    private final LastProcessedRepository lastProcessedRepository;
    private final ProductToIgnoreRepository productToIgnoreRepository;
    private final MarketTransactionRepository marketTransactionRepository;
    private final IndustryJobRepository industryJobRepository;
    private final ProductRepository productRepository;
    private final WarehouseItemRepository warehouseItemRepository;
    private final ItemRepository itemRepository;
    private final ContractHeaderRepository contractHeaderRepository;
    private final ContractDetailRepository contractDetailRepository;
    private final Warehouse warehouse;
    private final WarehouseListener warehouseListener;
    private final ExtraCostService extraCostService;
    
    private final ModelMapper modelMapper = new ModelMapper();
    
    public void save(WarehouseItemDTO warehouseItemDTO) {
        WarehouseItem warehouseItem = modelMapper.map(warehouseItemDTO, WarehouseItem.class);
        
        warehouseItemRepository.save(warehouseItem);
    }
    
    public List<WarehouseItemDTO> getWarehouse() {
        List<WarehouseItem> warehouseItems = warehouseItemRepository.findAll();
        List<WarehouseItemDTO> warehouseItemDTOs = new LinkedList<>();
        
        for (WarehouseItem warehouseItem : warehouseItems) {
            WarehouseItemDTO warehouseItemDTO = modelMapper.map(warehouseItem, WarehouseItemDTO.class);
            
            String name = itemRepository.findById(warehouseItem.getItemId())
                                        .map(Item::getName)
                                        .orElseThrow(() -> new RuntimeException(
                                                "Type not found for item id: " + warehouseItem.getItemId()));
            
            warehouseItemDTO.setName(name);
            
            warehouseItemDTOs.add(warehouseItemDTO);
        }
        
        return warehouseItemDTOs;
    }
    
    /**
     * Process all the market transactions, industry jobs and contracts.  This will sort them by timestamp and process
     * them in order.  This is done to ensure that the warehouse is always in a consistent state.
     */
    @Transactional
    public void processAll() {
        List<MarketTransaction> marketTransactions = marketTransactionRepository.findUnprocessed();
        List<Timestamped> allEvents = new LinkedList<>(marketTransactions);
        allEvents.addAll(industryJobRepository.findUnprocessed());
        allEvents.addAll(contractHeaderRepository.findProcessable());
        allEvents.sort(Comparator.comparing(Timestamped::getTimestamp));
        
        allEvents.forEach(t -> {
            if (t instanceof MarketTransaction) {
                MarketTransactionDTO marketTransaction = modelMapper.map(t, MarketTransactionDTO.class);
                processMarketTransaction(marketTransaction);
                warehouseListener.marketTransactionProcessed(marketTransaction);
            }
            else if (t instanceof IndustryJob) {
                IndustryJobDTO industryJob = modelMapper.map(t, IndustryJobDTO.class);
                processIndustryJob(industryJob);
                warehouseListener.industryJobProcessed(industryJob);
            }
            else if (t instanceof ContractHeader) {
                ContractHeaderDTO contractHeader = modelMapper.map(t, ContractHeaderDTO.class);
                processContract(contractHeader);
                warehouseListener.contractProcessed(contractHeader);
            }
        });
    }
    
    /**
     * Process a market transaction.  This is either a buy or sell order.  If it is a sell order, we need to remove the
     * quantity from the warehouse.  If it is a buy order, we need to add the quantity to the warehouse along with it's
     * cost
     *
     * @param marketTransaction The market transaction
     */
    public void processMarketTransaction(MarketTransactionDTO marketTransaction) {
        LastProcessedKey key = LastProcessedKey.builder()
                                               .objectId(marketTransaction.getTransactionId())
                                               .objectType(MARKET_TRANSACTION_NAME)
                                               .build();
        
        //TODO: this query should be rendered moot now that we're doing it in the database in the processAll method
        if (lastProcessedRepository.findById(key).isPresent()) {
            log.info("Market transaction {} already processed", marketTransaction.getTransactionId());
            return;
        }
        
        Double itemValue = 0.0;
        
        if (marketTransaction.getIsBuy()) {
            warehouse.addItem(marketTransaction.getTypeId(), marketTransaction.getQuantity(),
                    marketTransaction.getUnitPrice());
        }
        else {
            WarehouseItem warehouseItem = warehouse.getWarehouseItem(marketTransaction.getTypeId());
            if (warehouseItem != null) {
                warehouse.removeItem(marketTransaction.getTypeId(), marketTransaction.getQuantity());
            }
            else {
                log.warn("No warehouse item found for sell order: {}", marketTransaction.getTransactionId());
            }
        }
        
        //I want buy orders to have a null profit, not some potentially non-zero value
        if (!marketTransaction.getIsBuy()) {
            Double transactionAmount = marketTransaction.getUnitPrice();
            Double profitMargin = transactionAmount - itemValue;
            marketTransaction.setProfitMargin(profitMargin * marketTransaction.getQuantity());
        }

        marketTransactionRepository.save(modelMapper.map(marketTransaction, MarketTransaction.class));

        LastProcessed lastProcessed = LastProcessed.builder()
                                                   .id(key)
                                                   .build();
        lastProcessedRepository.save(lastProcessed);
    }

    /**
     * Dispatch the industry job to the appropriate processing method
     *
     * @param industryJob The industry job
     */
    public void processIndustryJob(IndustryJobDTO industryJob) {
        Optional<ProductToIgnore> productToIgnore = productToIgnoreRepository.findById(industryJob.getProductTypeId());
        if (productToIgnore.isPresent()) {
            lastProcessedRepository.save(LastProcessed.builder()
                                                      .id(LastProcessedKey.builder()
                                                                          .objectId(industryJob.getJobId())
                                                                          .objectType(INDUSTRY_JOB_NAME)
                                                                          .build()).build());
            return;
        }

        //TODO: this query should be rendered moot now that we're doing it in the database in the processAll method
        Optional<LastProcessed> lastProcessed = lastProcessedRepository.findById_ObjectId(industryJob.getJobId());
        if (lastProcessed.isPresent()) {
            log.warn("Job already processed: {}", industryJob.getJobId());
            return;
        }

        if (industryJob.getActivityId() != null) {
            boolean shouldMarkProcessed = switch (industryJob.getActivityId()) {
                case 1 -> processManufacturingJob(industryJob);
                case 5 -> processCopyJob(industryJob);
                case 8 -> processInventionJob(industryJob);
                default -> warnAndGiveTrue(industryJob);
            };

            if (shouldMarkProcessed) {
                lastProcessedRepository.save(LastProcessed.builder()
                                                          .id(LastProcessedKey.builder()
                                                                              .objectId(industryJob.getJobId())
                                                                              .objectType(INDUSTRY_JOB_NAME)
                                                                              .build()).build());
            }
        }
    }

    private static boolean warnAndGiveTrue(IndustryJobDTO industryJob) {
        log.warn("Unknown industry job activity id: {}", industryJob.getActivityId());
        return true;
    }

    /**
     * Process the contract.  This will fetch the details and process them similarly to how market transactions are
     * processed. It will attempt to 'stack the items' in the contract since they are not always in the same stack and
     * the contract preserves that state.
     *
     * @param contractHeader The contract header
     */
    private void processContract(ContractHeaderDTO contractHeader) {
        LastProcessedKey key = LastProcessedKey.builder()
                                               .objectId(contractHeader.getContractId())
                                               .objectType("contract")
                                               .build();

        //TODO: this query should be rendered moot now that we're doing it in the database in the processAll method
        if (lastProcessedRepository.findById(key).isPresent()) {
            log.info("Contract {} already processed", contractHeader.getContractId());
            return;
        }

        //Stack the items in the contract
        List<ContractDetail> details = contractDetailRepository.findByContractId(contractHeader.getContractId());
        Map<Long, Long> stackedItems = details.stream()
                                              .collect(Collectors.groupingBy(ContractDetail::getTypeId,
                                                      Collectors.summingLong(ContractDetail::getQuantity)));

        //Process the items in the contract
        for (Map.Entry<Long, Long> entry : stackedItems.entrySet()) {
            Long typeId = entry.getKey();
            Long quantity = entry.getValue();

            warehouse.addItem(typeId, quantity, contractHeader.getPrice() / quantity);
        }
        
        lastProcessedRepository.save(LastProcessed.builder()
                                                  .id(key)
                                                  .build());
    }
    
    /**
     * Process a manufacturing job.  We need to find the old cost per item, multiply it by the quantity, add the new
     * total cost and then divide all that by the totalQuantity + totalRuns.
     *
     * @param industryJob The industry job
     */
    private boolean processManufacturingJob(IndustryJobDTO industryJob) {
        Long productTypeId = industryJob.getProductTypeId();
        
        Product product = productRepository.findById(productTypeId)
                                           .orElseThrow(() -> new RuntimeException(
                                                   "Product not found for product type id: " + productTypeId));
        double cost = manufacturingCostForJob(industryJob, product);
        double costPerItem = cost / (double) (industryJob.getRuns() * product.getMakeTypeAmount());
        warehouse.addItem(productTypeId, industryJob.getRuns(), costPerItem);
        return true;
    }
    
    /**
     * Process a copy job.  This is simply setting the cost of the BPC to that of the industry job cost.
     *
     * @param industryJob The industry job
     */
    private boolean processCopyJob(IndustryJobDTO industryJob) {
        long totalBluePrints = industryJob.getRuns() * industryJob.getLicensedRuns();
        double costPerItem = industryJob.getCost() / totalBluePrints;
        warehouse.addItem(industryJob.getProductTypeId(), totalBluePrints, costPerItem);
        return true;
    }
    
    /**
     * Process an invention job.  We need to find the old cost per item, multiply it by the quantity, add the new total
     * cost and then divide all that by the totalQuantity + totalSuccessfulRuns.
     *
     * @param industryJob The industry job
     */
    private boolean processInventionJob(IndustryJobDTO industryJob) {
        if ("active".equalsIgnoreCase(industryJob.getStatus())) {
            return false;
        }
        
        Long t2BpcId = industryJob.getProductTypeId();
        Long t1BpcId = industryJob.getBlueprintTypeId();
        
        //Get the product, tells us the inputs for the invention job
        Product product = productRepository.findById(t2BpcId)
                                           .orElseThrow(() -> new RuntimeException(
                                                   "Product not found for product type id: " + t2BpcId));
        
        double newTotal = 0d;
        //        for(int idx = 0; idx < industryJob.getRuns(); idx++) {
        for (InventionItem inventionItem : product.getInventionItems()) {
            double cost = warehouse.removeItem(
                    inventionItem.getItem().getItemId(), inventionItem.getQuantity() * industryJob.getRuns());
            newTotal += cost;
            log.debug("Removed {} of product item {} with cost {}", inventionItem.getQuantity() * industryJob.getRuns(),
                    inventionItem.getItem().getName(), cost);
        }
        newTotal += warehouse.removeItem(t1BpcId, industryJob.getRuns());
        //        }
        long successfulRuns = industryJob.getSuccessfulRuns() == 0 ? 1L :
                industryJob.getSuccessfulRuns() * product.getMakeTypeAmount();
        double newCostPerItem = newTotal / successfulRuns;
        warehouse.addItem(t2BpcId, industryJob.getSuccessfulRuns() * product.getMakeTypeAmount(), newCostPerItem);
        
        return true;
    }
    
    /**
     * Calculate the cost of manufacturing a job and pull items out of the warehouse.
     *
     * @param industryJob The industry job
     * @param product The product
     *
     * @return The cost of manufacturing the job
     */
    private double manufacturingCostForJob(IndustryJobDTO industryJob, Product product) {
        double result = 0.0;
        
        for (ProductItem productItem : product.getProductItems()) {
            result += warehouse.removeItem(productItem.getItem().getItemId(),
                    productItem.getQuantity() * industryJob.getRuns());
        }
        
        double bpcCost = warehouse.removeItem(product.getMakeType(), industryJob.getRuns());
        result += bpcCost;
        result += industryJob.getCost();
        result += extraCostService.extraCost(product) * industryJob.getRuns();
        
        return result;
    }
}
