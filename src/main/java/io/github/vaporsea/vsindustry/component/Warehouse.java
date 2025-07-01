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

import io.github.vaporsea.vsindustry.domain.WarehouseItem;
import io.github.vaporsea.vsindustry.domain.WarehouseItemRepository;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Component
public class Warehouse {

    private final WarehouseItemRepository warehouseItemRepository;

    @Setter
    @Getter
    private boolean allowNegativeQuantities = false;
    
    public WarehouseItem getWarehouseItem(Long itemId) {
        return warehouseItemRepository.findById(itemId).orElse(
                WarehouseItem.builder()
                             .itemId(itemId)
                             .costPerItem(0.0D)
                             .quantity(0L)
                             .build()
        );
    }

    @Transactional
    public double removeItem(Long itemId, Long quantity) {
        return warehouseItemRepository.findById(itemId)
                                      .map(warehouseItem -> {
                                          long newQuantity = warehouseItem.getQuantity() - quantity;
                                          if (newQuantity <= 0 && !allowNegativeQuantities) {
                                              // If negative quantities are not allowed, set to 0
                                              warehouseItemRepository.save(WarehouseItem.builder()
                                                                                        .itemId(itemId)
                                                                                        .costPerItem(
                                                                                                warehouseItem.getCostPerItem())
                                                                                        .quantity(0L)
                                                                                        .build());
                                          }
                                          else {
                                              // Either the quantity is positive or negative quantities are allowed
                                              warehouseItem.setQuantity(newQuantity);
                                              warehouseItemRepository.save(warehouseItem);
                                          }

                                          return warehouseItem.getCostPerItem() == null ? 0D :
                                                  (double) warehouseItem.getCostPerItem() * quantity;
                                      })
                                      .orElse(0D);
    }

    @Transactional
    public void addItem(Long itemId, Long quantity, Double costPerItem) {
        warehouseItemRepository.findById(itemId)
                               .ifPresentOrElse(warehouseItem -> {
                                           Double newCost = rollingAverage(warehouseItem.getCostPerItem(), warehouseItem.getQuantity(),
                                                   costPerItem == null ? 0D : costPerItem, quantity);
                                           warehouseItem.setQuantity(warehouseItem.getQuantity() + quantity);
                                           warehouseItem.setCostPerItem(newCost);
                                           warehouseItemRepository.save(warehouseItem);
                                       }, () ->
                                               warehouseItemRepository.save(WarehouseItem.builder()
                                                                                         .itemId(itemId)
                                                                                         .quantity(quantity)
                                                                                         .costPerItem(costPerItem == null ? 0D :
                                                                                                 costPerItem)
                                                                                         .build())
                               );
    }

    /**
     * Calculate the rolling average of a cost
     *
     * @param oldAverage The old cost per item... or old average
     * @param oldQuantity The old quantity
     * @param newCost The new cost per item
     * @param newQuantity The new quantity
     *
     * @return The new average
     */
    private Double rollingAverage(Double oldAverage, Long oldQuantity, Double newCost, Long newQuantity) {
        return (oldAverage * oldQuantity + newCost * newQuantity) / (oldQuantity + newQuantity);
    }
}
