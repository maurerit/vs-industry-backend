package io.github.vaporsea.vsindustry.component;

import io.github.vaporsea.vsindustry.domain.WarehouseItem;
import io.github.vaporsea.vsindustry.domain.WarehouseItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Component
public class Warehouse {
    
    private final WarehouseItemRepository warehouseItemRepository;
    
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
                                          if (newQuantity <= 0) {
                                              warehouseItemRepository.save(WarehouseItem.builder()
                                                                                        .itemId(itemId)
                                                                                        .costPerItem(
                                                                                                warehouseItem.getCostPerItem())
                                                                                        .quantity(0L)
                                                                                        .build());
                                          }
                                          else {
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
                                           warehouseItem.setQuantity(warehouseItem.getQuantity() + quantity);
                                           warehouseItem.setCostPerItem(costPerItem == null ? 0D : costPerItem);
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
}
