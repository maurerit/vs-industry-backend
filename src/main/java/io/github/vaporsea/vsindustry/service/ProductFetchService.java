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

import io.github.vaporsea.vsindustry.component.Warehouse;
import io.github.vaporsea.vsindustry.contract.ActivityMaterialDTO;
import io.github.vaporsea.vsindustry.contract.ActivityMaterialsDTO;
import io.github.vaporsea.vsindustry.contract.BlueprintDataDTO;
import io.github.vaporsea.vsindustry.contract.BlueprintDetailsDTO;
import io.github.vaporsea.vsindustry.contract.ExtraCostDTO;
import io.github.vaporsea.vsindustry.contract.Page;
import io.github.vaporsea.vsindustry.contract.ProductDTO;
import io.github.vaporsea.vsindustry.contract.TransactionCostsDTO;
import io.github.vaporsea.vsindustry.domain.ExtraCost;
import io.github.vaporsea.vsindustry.domain.InventionItem;
import io.github.vaporsea.vsindustry.domain.Item;
import io.github.vaporsea.vsindustry.domain.ItemRepository;
import io.github.vaporsea.vsindustry.domain.MarketStat;
import io.github.vaporsea.vsindustry.domain.MarketStatId;
import io.github.vaporsea.vsindustry.domain.MarketStatRepository;
import io.github.vaporsea.vsindustry.domain.Product;
import io.github.vaporsea.vsindustry.domain.ProductItem;
import io.github.vaporsea.vsindustry.domain.ReactionItem;
import io.github.vaporsea.vsindustry.domain.ProductRepository;
import io.github.vaporsea.vsindustry.domain.ProductSearch;
import io.github.vaporsea.vsindustry.domain.WarehouseItem;
import io.github.vaporsea.vsindustry.util.TradeHub;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Service
public class ProductFetchService {

    private final ProductRepository productRepository;
    private final Warehouse warehouse;
    private final MarketStatRepository marketStatRepository;
    private final ExtraCostService extraCostService;
    private final ItemRepository itemRepository;
    private final ModelMapper modelMapper;

    public Page<ProductDTO> getProducts(Pageable pageable, ProductSearch search) {
        org.springframework.data.domain.Page<Product> products = productRepository.findAll(search, pageable);
        List<ProductDTO> productDTOs = products.stream()
                                               .map(product -> ProductDTO.builder()
                                                                         .itemId(product.getItemId())
                                                                         .name(product.getName())
                                                                         .description(product.getDescription())
                                                                         .build())
                                               .toList();

        //Hydrate the product to get its build cost
        for (ProductDTO productDTO : productDTOs) {
            Product product = productRepository.findById(productDTO.getItemId()).orElse(null);
            if (product == null) {
                productDTO.setCost(warehouse.getWarehouseItem(productDTO.getItemId()).getCostPerItem());
            }
            else {
                //Sum up all the Material Costs, Invention Costs, and Reaction Costs
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
                // Add reaction costs if there are any
                if (product.getReactionItems() != null) {
                    cost += product.getReactionItems().stream()
                                   .mapToDouble(
                                           reactionItem -> warehouse.getWarehouseItem(reactionItem.getItem().getItemId())
                                                                    .getCostPerItem() * reactionItem.getQuantity() /
                                                   product.getMakeTypeAmount())
                                   .sum();
                }
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
                                                          .cost(warehouse.getWarehouseItem(product.getMakeType()).getCostPerItem())
                                                          .build()
                               )
                               .transactionCosts(buildTransactionCosts(product))
                               .activityMaterials(buildActivityMaterials(product))
                               .build();
    }

    private TransactionCostsDTO buildTransactionCosts(Product product) {
        WarehouseItem warehouseItem = warehouse.getWarehouseItem(product.getItemId());
        MarketStat marketStat = marketStatRepository.findById(
                MarketStatId.builder()
                            .itemId(product.getItemId())
                            .systemId(TradeHub.AMARR.getSystemId()) // TODO: Make this configurable
                            .build()
        ).orElse(MarketStat.builder().build());

        Double brokersFee = marketStat.getSellMinimum() != null ?
                marketStat.getSellMinimum().doubleValue() * 0.0137 : // Assuming 1.37% broker fee
                (warehouseItem.getCostPerItem() + warehouseItem.getCostPerItem() * .3) * 0.0137; // Fallback to warehouse item cost + 30% if no market stat

        Double salesTax = marketStat.getSellMinimum() != null ?
                marketStat.getSellMinimum().doubleValue() * 0.03375 : // Assuming 3.375% sales tax
                (warehouseItem.getCostPerItem() + warehouseItem.getCostPerItem() * .3) * 0.03375; // Fallback to warehouse item cost + 30% if no market stat

        return TransactionCostsDTO.builder()
                                  .brokersFee(brokersFee) // TODO: Assuming hard coded broker fee
                                  .salesTax(salesTax) // TODO: Assuming hard coded sales tax
                                  .extraCosts(getExtraCosts(product))
                                  .build();
    }

    private List<ExtraCostDTO> getExtraCosts(Product product) {
        List<ExtraCostDTO> result = new ArrayList<>();

        List<ExtraCost> extraCosts = extraCostService.getExtraCosts(product.getItemId());
        for (ExtraCost extraCost : extraCosts) {
            ExtraCostDTO extraCostDTO = modelMapper.map(extraCost, ExtraCostDTO.class);
            extraCostDTO.setName(itemRepository.findById(extraCost.getItemId()).orElse(new Item()).getName());
            result.add(extraCostDTO);
        }

        return result;
    }

    private ActivityMaterialsDTO buildActivityMaterials(Product product) {
        return ActivityMaterialsDTO.builder()
                                   .invention(buildInventionActivityMaterials(product))
                                   .manufacturing(buildManufacturingActivityMaterials(product))
                                   .copying(List.of())
                                   .reaction(buildReactionActivityMaterials(product))
                                   .build();
    }

    private List<ActivityMaterialDTO> buildReactionActivityMaterials(Product product) {
        if (product.getReactionItems() == null) {
            return List.of();
        }
        return product.getReactionItems().stream()
                      .map(this::buildActivityMaterial)
                      .toList();
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
                                  .marketPrice(getSellMinimum(inventionItem.getItem().getItemId()))
                                  .build();
    }

    private ActivityMaterialDTO buildActivityMaterial(ProductItem productItem) {
        return ActivityMaterialDTO.builder()
                                  .typeid(productItem.getItem().getItemId())
                                  .name(productItem.getItem().getName())
                                  .quantity(productItem.getQuantity())
                                  .price(warehouse.getWarehouseItem(productItem.getItem().getItemId()).getCostPerItem())
                                  .marketPrice(getSellMinimum(productItem.getItem().getItemId()))
                                  .build();
    }

    private ActivityMaterialDTO buildActivityMaterial(ReactionItem reactionItem) {
        return ActivityMaterialDTO.builder()
                                  .typeid(reactionItem.getItem().getItemId())
                                  .name(reactionItem.getItem().getName())
                                  .quantity(reactionItem.getQuantity())
                                  .price(warehouse.getWarehouseItem(reactionItem.getItem().getItemId()).getCostPerItem())
                                  .marketPrice(getSellMinimum(reactionItem.getItem().getItemId()))
                                  .build();
    }

    private Double getSellMinimum(Long itemId) {
        MarketStat marketStat = marketStatRepository.findById(
                MarketStatId.builder()
                            .itemId(itemId)
                            .systemId(TradeHub.AMARR.getSystemId())  // TODO: Make this configurable
                            .build()
        ).orElse(MarketStat.builder().build());

        WarehouseItem warehouseItem = warehouse.getWarehouseItem(itemId);

        return marketStat.getSellMinimum() != null ?
                marketStat.getSellMinimum().doubleValue() :
                (warehouseItem.getCostPerItem() +
                         warehouseItem.getCostPerItem() * .3);
    }
}
