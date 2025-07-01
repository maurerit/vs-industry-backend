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

package io.github.vaporsea.vsindustry.component;

import io.github.vaporsea.vsindustry.domain.IndustryActivityProbability;
import io.github.vaporsea.vsindustry.domain.IndustryActivityProbabilityRepository;
import io.github.vaporsea.vsindustry.domain.IndustryActivityProduct;
import io.github.vaporsea.vsindustry.domain.IndustryActivityProductRepository;
import io.github.vaporsea.vsindustry.domain.IndustryJob;
import io.github.vaporsea.vsindustry.domain.IndustryJobRepository;
import io.github.vaporsea.vsindustry.domain.WarehouseItem;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * A warehouse implementation that encapsulates the current Warehouse and adds
 * logic to estimate BPC results based on probabilities.
 * 
 * @author Matt Maurer <br>
 * @since 6/9/2024
 */
@RequiredArgsConstructor
@Component
public class OverlayWarehouse {

    private final Warehouse warehouse;
    private final IndustryJobRepository industryJobRepository;
    private final IndustryActivityProbabilityRepository industryActivityProbabilityRepository;
    private final IndustryActivityProductRepository industryActivityProductRepository;

    @Setter
    @Getter
    private boolean allowNegativeQuantities = false;

    public List<WarehouseItem> getAllItems() {
        List<WarehouseItem> items = warehouse.getAllItems();

        items.forEach(this::updateInventedBpcs);

        return items;
    }

    /**
     * Get a warehouse item, with additional logic for blueprints.
     * If the item is a blueprint, search for industry jobs for the item
     * and estimate how many will result in BPCs based on probabilities.
     * 
     * @param itemId The ID of the item to get
     * @return The warehouse item
     */
    public WarehouseItem getWarehouseItem(Long itemId) {
        WarehouseItem item = warehouse.getWarehouseItem(itemId);

        updateInventedBpcs(item);

        return item;
    }

    private void updateInventedBpcs(WarehouseItem item) {
        // Check if this is a blueprint by looking for industry jobs with this blueprint type ID
        List<IndustryJob> jobs = industryJobRepository.findUnprocessed()
                .stream()
                .filter(job -> job.getProductTypeId().equals(item.getItemId()) && job.getActivityId().equals(8L))
                .toList();

        if (!jobs.isEmpty()) {
            // This is a blueprint with active industry jobs
            long totalEstimatedBpcs = 0;
            double totalCost = 0.0;

            for (IndustryJob job : jobs) {
                // Get the probability for this job
                IndustryActivityProbability probability = industryActivityProbabilityRepository
                        .findById_TypeIdAndId_ActivityIdAndId_ProductTypeId(
                                job.getBlueprintTypeId(),
                                job.getActivityId(),
                                job.getProductTypeId())
                        .orElse(null);

                if (probability != null) {
                    // Calculate estimated BPCs based on probability
                    long runs = job.getRuns();
                    // Look up the quantity from the INDUSTRY_ACTIVITY_PRODUCTS table
                    long multiplier = industryActivityProductRepository
                            .findById_ProductTypeId(job.getProductTypeId())
                            .map(IndustryActivityProduct::getQuantity)
                            .orElse(1L); // Default to 1 if not found
                    long estimatedBpcs = Math.round(runs * probability.getProbability()) * multiplier;
                    totalEstimatedBpcs += estimatedBpcs;

                    // Calculate cost contribution
                    if (job.getCost() != null) {
                        totalCost += job.getCost();
                    }
                }
            }

            // Add the estimated BPCs to the item quantity
            item.setQuantity(item.getQuantity() + totalEstimatedBpcs);

            // Update cost per item if there are estimated BPCs
            if (totalEstimatedBpcs > 0 && totalCost > 0) {
                double costPerItem = totalCost / totalEstimatedBpcs;
                if (item.getCostPerItem() == 0.0) {
                    item.setCostPerItem(costPerItem);
                } else {
                    // Use weighted average for cost
                    double originalQuantity = item.getQuantity() - totalEstimatedBpcs;
                    double originalCost = originalQuantity * item.getCostPerItem();
                    double newCost = originalCost + totalCost;
                    double newQuantity = item.getQuantity();
                    item.setCostPerItem(newCost / newQuantity);
                }
            }
        }
    }

    /**
     * Remove an item from the warehouse.
     * 
     * @param itemId The ID of the item to remove
     * @param quantity The quantity to remove
     * @return The cost of the removed items
     */
    @Transactional
    public double removeItem(Long itemId, Long quantity) {
        // For removal, we just delegate to the underlying warehouse
        warehouse.setAllowNegativeQuantities(allowNegativeQuantities);
        return warehouse.removeItem(itemId, quantity);
    }

    /**
     * Add an item to the warehouse.
     * 
     * @param itemId The ID of the item to add
     * @param quantity The quantity to add
     * @param costPerItem The cost per item
     */
    @Transactional
    public void addItem(Long itemId, Long quantity, Double costPerItem) {
        // For addition, we just delegate to the underlying warehouse
        warehouse.addItem(itemId, quantity, costPerItem);
    }
}
