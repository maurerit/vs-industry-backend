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

import java.util.List;

import io.github.vaporsea.vsindustry.domain.IndustryActivityProduct;
import io.github.vaporsea.vsindustry.domain.IndustryActivityProductRepository;
import org.springframework.stereotype.Service;

import io.github.vaporsea.vsindustry.contract.fuzzwork.BlueprintDTO;
import io.github.vaporsea.vsindustry.domain.InventionItem;
import io.github.vaporsea.vsindustry.domain.Item;
import io.github.vaporsea.vsindustry.domain.ItemRepository;
import io.github.vaporsea.vsindustry.domain.Product;
import io.github.vaporsea.vsindustry.domain.ProductItem;
import io.github.vaporsea.vsindustry.domain.ProductRepository;
import lombok.RequiredArgsConstructor;

/**
 * @author Matt Maurer <br>
 * @since 6/17/2024
 */
@RequiredArgsConstructor
@Service
public class ProductSetupService {
    
    private final ProductRepository productRepository;
    private final ItemRepository itemRepository;
    private final IndustryActivityProductRepository industryActivityProductRepository;
    
    public void setupProduct(BlueprintDTO blueprintDTO) {
        checkT2AndCreateInventionitemsForBlueprint(blueprintDTO);
        
        createProductItemsForBlueprint(blueprintDTO);
    }
    
    private void checkT2AndCreateInventionitemsForBlueprint(BlueprintDTO blueprintDTO) {
        //Check the tech level and create a product for the blueprint if it's tech level 2
        if (blueprintDTO.getBlueprintDetails().getTechLevel() == 2) {
            IndustryActivityProduct industryActivityProduct =
                    industryActivityProductRepository.findById_ProductTypeId((long) blueprintDTO.getRequestedid())
                                                     .orElseThrow();
            
            //T2 Build the Blueprint as a product
            List<InventionItem> inventionItems = blueprintDTO.getActivityMaterials().get("8").stream()
                                                             .map(material -> InventionItem.builder()
                                                                                           .item(Item.builder()
                                                                                                     .itemId(material.getTypeid())
                                                                                                     .name(material.getName())
                                                                                                     .build()
                                                                                           )
                                                                                           .quantity(
                                                                                                   material.getQuantity())
                                                                                           .build())
                                                             .toList();
            
            itemRepository.save(Item.builder()
                                    .itemId(blueprintDTO.getBlueprintTypeID())
                                    .name(blueprintDTO.getBlueprintDetails().getProductTypeName() + " Blueprint")
                                    .build());
            
            Product t2BluePrint = Product.builder()
                                         .name(blueprintDTO.getBlueprintDetails().getProductTypeName() + " Blueprint")
                                         .itemId(industryActivityProduct.getId().getTypeId())
                                         .makeTypeAmount(10L)
                                         .makeType(blueprintDTO.getBlueprintTypeID())
                                         .inventionItems(inventionItems)
                                         .build();
            
            for (InventionItem inventionItem : t2BluePrint.getInventionItems()) {
                inventionItem.setProduct(t2BluePrint);
            }
            
            productRepository.save(t2BluePrint);
        }
    }
    
    private void createProductItemsForBlueprint(BlueprintDTO blueprintDTO) {
        //Build the Blueprint as a product
        List<ProductItem> materials = blueprintDTO.getActivityMaterials().get("1").stream()
                                                  .map(material -> ProductItem.builder()
                                                                              .item(Item.builder()
                                                                                        .itemId(material.getTypeid())
                                                                                        .name(material.getName())
                                                                                        .build())
                                                                              .quantity(material.getQuantity())
                                                                              .build())
                                                  .toList();
        
        Product product = Product.builder()
                                 .name(blueprintDTO.getBlueprintDetails().getProductTypeName())
                                 .itemId(blueprintDTO.getBlueprintDetails().getProductTypeID())
                                 .makeType((long) blueprintDTO.getRequestedid())
                                 .makeTypeAmount(blueprintDTO.getBlueprintDetails().getProductQuantity())
                                 .productItems(materials)
                                 .build();
        
        for (ProductItem productItem : product.getProductItems()) {
            productItem.setProduct(product);
        }
        
        productRepository.save(product);
    }
}
