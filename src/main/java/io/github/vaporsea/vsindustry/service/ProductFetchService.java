package io.github.vaporsea.vsindustry.service;

import io.github.vaporsea.vsindustry.component.Warehouse;
import io.github.vaporsea.vsindustry.contract.ActivityMaterialDTO;
import io.github.vaporsea.vsindustry.contract.ActivityMaterialsDTO;
import io.github.vaporsea.vsindustry.contract.BlueprintDataDTO;
import io.github.vaporsea.vsindustry.contract.BlueprintDetailsDTO;
import io.github.vaporsea.vsindustry.domain.InventionItem;
import io.github.vaporsea.vsindustry.domain.Product;
import io.github.vaporsea.vsindustry.domain.ProductItem;
import io.github.vaporsea.vsindustry.domain.ProductRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class ProductFetchService {
    
    private final ProductRepository productRepository;
    private final Warehouse warehouse;
    
    public BlueprintDataDTO getProduct(Long itemId) {
        Product product = productRepository.findById(itemId).orElse(null);
        if (product != null) {
            return BlueprintDataDTO.builder()
                                   .blueprintDetails(
                                           BlueprintDetailsDTO.builder()
                                                              .maxProductionLimit(product.getMakeTypeAmount())
                                                              .productTypeID(product.getItemId())
                                                              .productTypeName(product.getName())
                                                              .productMakeTypeID(product.getMakeType())
                                                              .build()
                                   )
                                   .activityMaterials(buildActivityMaterials(product))
                                   .build();
        }
        throw new EntityNotFoundException("Product with id " + itemId + " not found");
    }
    
    private ActivityMaterialsDTO buildActivityMaterials(Product product) {
        return ActivityMaterialsDTO.builder()
                                   .invention(buildInventionActivityMaterials(product))
                                   .manufacturing(buildManufacturingActivityMaterials(product))
                                   .copying(List.of())
                                   .build();
    }
    
    private List<ActivityMaterialDTO> buildInventionActivityMaterials(Product product) {
        return product.getInventionItems().stream()
                      .map(this::buildActivityMaterial)
                      .toList();
    }
    
    private List<ActivityMaterialDTO> buildManufacturingActivityMaterials(Product product) {
        return product.getProductItems().stream()
                      .map(this::buildActivityMaterial)
                      .toList();
    }
    
    private ActivityMaterialDTO buildActivityMaterial(InventionItem inventionItem) {
        return ActivityMaterialDTO.builder()
                                  .typeid(inventionItem.getItem().getItemId())
                                  .name(inventionItem.getItem().getName())
                                  .quantity(inventionItem.getQuantity())
                                  .price(warehouse.getWarehouseItem(inventionItem.getItem().getItemId())
                                                  .getCostPerItem())
                                  .build();
    }
    
    private ActivityMaterialDTO buildActivityMaterial(ProductItem productItem) {
        return ActivityMaterialDTO.builder()
                                  .typeid(productItem.getItem().getItemId())
                                  .name(productItem.getItem().getName())
                                  .quantity(productItem.getQuantity())
                                  .price(warehouse.getWarehouseItem(productItem.getItem().getItemId()).getCostPerItem())
                                  .build();
    }
}
