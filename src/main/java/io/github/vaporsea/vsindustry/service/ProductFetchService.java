package io.github.vaporsea.vsindustry.service;

import io.github.vaporsea.vsindustry.component.Warehouse;
import io.github.vaporsea.vsindustry.contract.ActivityMaterialDTO;
import io.github.vaporsea.vsindustry.contract.ActivityMaterialsDTO;
import io.github.vaporsea.vsindustry.contract.BlueprintDataDTO;
import io.github.vaporsea.vsindustry.contract.BlueprintDetailsDTO;
import io.github.vaporsea.vsindustry.contract.Page;
import io.github.vaporsea.vsindustry.contract.ProductDTO;
import io.github.vaporsea.vsindustry.domain.InventionItem;
import io.github.vaporsea.vsindustry.domain.Product;
import io.github.vaporsea.vsindustry.domain.ProductItem;
import io.github.vaporsea.vsindustry.domain.ProductRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class ProductFetchService {
    
    private final ProductRepository productRepository;
    private final Warehouse warehouse;
    
    public Page<ProductDTO> getProducts(Pageable pageable) {
        org.springframework.data.domain.Page<Product> products = productRepository.findAll(pageable);
        List<ProductDTO> productDTOs = products.stream()
                                               .map(product -> ProductDTO.builder()
                                                                         .itemId(product.getItemId())
                                                                         .name(product.getName())
                                                                         .description(product.getDescription())
                                                                         .build())
                                               .toList();
        
        //Hydrate the product to get it's build cost
        for (ProductDTO productDTO : productDTOs) {
            Product product = productRepository.findById(productDTO.getItemId()).orElse(null);
            if (product == null) {
                productDTO.setCost(warehouse.getWarehouseItem(product.getItemId()).getCostPerItem());
            }
            else {
                //Sum up all the Matherial Costs or Invention Costs
                double cost = product.getProductItems().stream()
                                     .mapToDouble(productItem ->
                                             warehouse.getWarehouseItem(productItem.getItem().getItemId())
                                                      .getCostPerItem() * productItem.getQuantity() /
                                                     product.getMakeTypeAmount())
                                     .sum();
                cost += product.getInventionItems().stream()
                               .mapToDouble(
                                       inventionItem -> warehouse.getWarehouseItem(inventionItem.getItem().getItemId())
                                                                 .getCostPerItem() * inventionItem.getQuantity() /
                                               product.getMakeTypeAmount())
                               .sum();
                productDTO.setCost(cost);
            }
        }
        
        return new Page<>(pageable.getPageNumber(), products.getTotalPages(), products.getTotalElements(), productDTOs);
    }
    
    public BlueprintDataDTO getProduct(Long itemId) {
        Product product = productRepository.findById(itemId).orElse(null);
        if (product != null) {
            return getBlueprintDataDTO(product);
        }
        throw new EntityNotFoundException("Product with id " + itemId + " not found");
    }
    
    private BlueprintDataDTO getBlueprintDataDTO(Product product) {
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
